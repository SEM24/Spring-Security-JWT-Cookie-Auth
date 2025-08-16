package com.security.fiverr.security.oauth2.service;

import com.security.fiverr.auth.model.dto.AuthResult;
import org.springframework.transaction.annotation.Transactional;

public interface OAuth2Service {
    @Transactional
    AuthResult processOAuthLogin(String email, String name, String providerId);
}
