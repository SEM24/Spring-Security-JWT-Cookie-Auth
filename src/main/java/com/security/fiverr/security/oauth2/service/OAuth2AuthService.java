package com.security.fiverr.security.oauth2.service;

import com.security.fiverr.auth.model.dto.AuthResult;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface OAuth2AuthService {
    @Transactional
    AuthResult processOAuth2Login(OAuth2User oAuth2User);

    AuthResult unlinkOAuth2Provider(String email, String provider);

    Set<String> getLinkedProviders(String email);
}
