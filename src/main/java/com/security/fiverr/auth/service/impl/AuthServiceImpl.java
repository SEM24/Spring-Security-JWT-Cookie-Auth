package com.security.fiverr.auth.service.impl;

import com.security.fiverr.auth.model.dto.AuthResult;
import com.security.fiverr.auth.model.dto.LoginRequest;
import com.security.fiverr.auth.model.dto.LogoutResponse;
import com.security.fiverr.auth.model.dto.RegisterRequest;
import com.security.fiverr.auth.service.AuthService;
import com.security.fiverr.exception.GlobalServiceException;
import com.security.fiverr.security.jwt.JwtService;
import com.security.fiverr.security.userdetails.UserDetailsImpl;
import com.security.fiverr.security.token.model.entity.RefreshToken;
import com.security.fiverr.security.token.service.RefreshTokenService;
import com.security.fiverr.security.token.service.TokenValidationService;
import com.security.fiverr.user.model.enitity.Role;
import com.security.fiverr.user.model.enitity.User;
import com.security.fiverr.user.repository.RoleRepository;
import com.security.fiverr.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final TokenValidationService tokenValidationService;
    private final AuthResponseBuilder authResponseBuilder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public AuthResult register(RegisterRequest request) {
        validateEmailNotExists(request.email());

        User user = createNewUser(request);
        User savedUser = userRepository.save(user);

        log.info("New user registered: {}", savedUser.getEmail());
        return authResponseBuilder.buildFullAuthResponse(savedUser, "Registration successful");
    }

    @Transactional
    @Override
    public AuthResult login(LoginRequest request) {
        Authentication authentication = authenticateUser(request);
        User user = getUserFromAuthentication(authentication);

        log.info("User logged in: {}", user.getEmail());

        // Delete old refresh tokens for this user
        refreshTokenService.deleteByUser(user);

        return authResponseBuilder.buildFullAuthResponse(user, "Authentication successful");
    }

    @Transactional
    @Override
    public AuthResult refreshAccessToken(HttpServletRequest request) {
        String refreshTokenValue = tokenValidationService.extractRefreshTokenFromRequest(request);
        RefreshToken refreshToken = refreshTokenService.validateAndRefresh(refreshTokenValue);

        User user = refreshToken.getUser();
        log.info("Token refreshed for user: {}", user.getEmail());

        return authResponseBuilder.buildAccessTokenOnlyResponse(user, "Token refreshed successfully");
    }

    @Transactional
    @Override
    public LogoutResponse logout(HttpServletRequest request) {
        String refreshTokenValue = jwtService.extractRefreshToken(request);

        if (refreshTokenValue != null) {
            refreshTokenService.findByToken(refreshTokenValue)
                    .ifPresent(refreshToken -> {
                        log.info("User logged out: {}", refreshToken.getUser().getEmail());
                        refreshTokenService.delete(refreshToken);
                    });
        }

        return createLogoutResponse();
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            //todo change
            throw new GlobalServiceException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }
    }

    private User createNewUser(RegisterRequest request) {
        Role userRole = roleRepository.findByName(request.role())
                .orElseThrow(() -> new GlobalServiceException(HttpStatus.NOT_FOUND,"Role not found: " + request.role()));
        return User.builder()
                .username(request.email())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(userRole)) //fixme or base role or set of roles
                .enabled(true) //fixme or false if needs to be activated via email
                .build();
    }

    private Authentication authenticateUser(LoginRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
    }

    private User getUserFromAuthentication(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private LogoutResponse createLogoutResponse() {
        ResponseCookie accessTokenCookie = jwtService.clearAccessTokenCookie();
        ResponseCookie refreshTokenCookie = jwtService.clearRefreshTokenCookie();

        return LogoutResponse.builder()
                .message("Logged out successfully")
                .accessTokenCookie(accessTokenCookie)
                .refreshTokenCookie(refreshTokenCookie)
                .build();
    }

}
