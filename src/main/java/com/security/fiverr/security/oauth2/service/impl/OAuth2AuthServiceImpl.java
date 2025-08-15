package com.security.fiverr.security.oauth2.service.impl;

import com.security.fiverr.auth.model.dto.AuthResult;
import com.security.fiverr.auth.service.impl.AuthResponseBuilder;
import com.security.fiverr.exception.GlobalServiceException;
import com.security.fiverr.security.oauth2.service.OAuth2AuthService;
import com.security.fiverr.security.token.service.RefreshTokenService;
import com.security.fiverr.user.model.enitity.ERole;
import com.security.fiverr.user.model.enitity.Role;
import com.security.fiverr.user.model.enitity.User;
import com.security.fiverr.user.repository.RoleRepository;
import com.security.fiverr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthServiceImpl implements OAuth2AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthResponseBuilder authResponseBuilder;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public AuthResult processOAuth2Login(OAuth2User oAuth2User) {
        String email = extractEmail(oAuth2User);
        String name = extractName(oAuth2User);
        String provider = determineProvider(oAuth2User);

        log.info("Processing OAuth2 login for email: {} from provider: {}", email, provider);

        // Search for user
        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateUserWithOAuth2Provider(existingUser, provider))
                .orElseGet(() -> createOAuth2User(email, name, provider));

        User savedUser = userRepository.save(user);

        // Delete old refresh tokens
        refreshTokenService.deleteByUser(savedUser);

        log.info("OAuth2 authentication successful for user: {} via {}", savedUser.getEmail(), provider);
        return authResponseBuilder.buildFullAuthResponse(savedUser, "OAuth2 authentication successful");
    }

    @Override
    public AuthResult unlinkOAuth2Provider(String email, String provider) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getOauth2Providers().size() <= 1 && user.getPassword() == null) {
            throw new GlobalServiceException(HttpStatus.BAD_REQUEST,
                    "Cannot unlink the only authentication method. Set a password first.");
        }

        user.getOauth2Providers().remove(provider);
        User savedUser = userRepository.save(user);

        log.info("OAuth2 provider {} unlinked for user: {}", provider, email);
        return authResponseBuilder.buildAccessTokenOnlyResponse(savedUser, "Provider unlinked successfully");
    }

    @Override
    public Set<String> getLinkedProviders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getOauth2Providers();
    }

    private String extractEmail(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        if (email == null || email.trim().isEmpty()) {
            throw new GlobalServiceException(HttpStatus.BAD_REQUEST,
                    "Email not provided by OAuth2 provider");
        }
        return email;
    }

    private String extractName(OAuth2User oAuth2User) {
        String name = oAuth2User.getAttribute("name");
        if (name == null || name.trim().isEmpty()) {
            // Fallback to login or email
            name = oAuth2User.getAttribute("login");
            if (name == null) {
                name = extractEmail(oAuth2User);
            }
        }
        return name;
    }

    private String determineProvider(OAuth2User oAuth2User) {
        // Google OAuth2 has "sub"
        if (oAuth2User.getAttributes().containsKey("sub") &&
                oAuth2User.getAttributes().containsKey("email_verified")) {
            return "google";
        }
        // GitHub OAuth2 has "login"
        else if (oAuth2User.getAttributes().containsKey("login") &&
                oAuth2User.getAttributes().containsKey("node_id")) {
            return "github";
        }

        log.warn("Unknown OAuth2 provider. Available attributes: {}",
                oAuth2User.getAttributes().keySet());
        return "unknown";
    }

    private User updateUserWithOAuth2Provider(User existingUser, String provider) {
        if (!existingUser.getOauth2Providers().contains(provider)) {
            existingUser.getOauth2Providers().add(provider);
            log.info("Added OAuth2 provider {} to existing user: {}", provider, existingUser.getEmail());
        }

        // If the email was not verified, but the OAuth2 provider verified it
        if (!existingUser.isEmailVerified()) {
            existingUser.setEmailVerified(true);
            log.info("Email verified via OAuth2 for user: {}", existingUser.getEmail());
        }

        return existingUser;
    }

    private User createOAuth2User(String email, String name, String provider) {
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new GlobalServiceException(HttpStatus.NOT_FOUND, "Default role USER not found"));

        User newUser = User.builder()
                .username(email)
                .email(email)
                .name(name)
                // Password is null - user can only login via OAuth2
                .password(null)
                .roles(Set.of(userRole))
                .oauth2Providers(Set.of(provider))
                .emailVerified(true) // OAuth2 providers usually already have email verification
                .enabled(true)
                .build();

        log.info("Created new OAuth2 user: {} via provider: {}", email, provider);
        return newUser;
    }
}
