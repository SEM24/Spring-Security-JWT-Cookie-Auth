package com.security.fiverr.security.oauth2.service.impl;

import com.security.fiverr.auth.model.dto.AuthResult;
import com.security.fiverr.auth.service.impl.AuthResponseBuilder;
import com.security.fiverr.security.oauth2.service.OAuth2Service;
import com.security.fiverr.security.token.service.RefreshTokenService;
import com.security.fiverr.user.model.enitity.AuthProviderType;
import com.security.fiverr.user.model.enitity.Role;
import com.security.fiverr.user.model.enitity.User;
import com.security.fiverr.user.repository.UserRepository;
import com.security.fiverr.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2ServiceImpl implements OAuth2Service {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final AuthResponseBuilder authResponseBuilder;

    @Transactional
    @Override
    public AuthResult processOAuthLogin(String email, String name, String providerId) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        String message;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            log.info("Found existing user with provider: {}", user.getProvider());

            // If local user - update to Google provider
            if (user.getProvider() == AuthProviderType.LOCAL) {
                user.setProvider(AuthProviderType.GOOGLE);
                user.setProviderId(providerId);
                user = userRepository.save(user);
                log.info("Updated user provider from LOCAL to GOOGLE");
                message = "Account linked with Google successfully";
            } else {
                message = "Welcome back! Signed in with Google";
            }
        } else {
            // Create a NEW user via Google OAuth
            log.info("Creating new user via Google OAuth for email: {}", email);

            user = createOAuthUser(email, name, providerId);
            user = userRepository.save(user);
            log.info("Created new Google user with ID: {}", user.getId());
            message = "Account created and signed in with Google";
        }

        refreshTokenService.deleteByUser(user);

        return authResponseBuilder.buildFullAuthResponse(user, message);
    }

    private User createOAuthUser(String email, String name, String providerId) {
        Role userRole = userService.getDefaultRole();

        return User.builder()
                .username(email)
                .email(email)
                .name(name)
                .password("") // Empty password for OAuth users
                .provider(AuthProviderType.GOOGLE)
                .providerId(providerId)
                .enabled(true)
                .emailVerified(true)
                .roles(Set.of(userRole))
                .build();
    }
}