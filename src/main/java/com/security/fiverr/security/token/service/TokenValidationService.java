package com.security.fiverr.security.token.service;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenValidationService {
    String extractRefreshTokenFromRequest(HttpServletRequest request);

    void validateRefreshTokenPresence(String token);
}
