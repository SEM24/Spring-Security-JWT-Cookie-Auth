package com.security.fiverr.auth.model.dto;

import lombok.Builder;
import org.springframework.http.ResponseCookie;

@Builder
public record AuthResult(
        AuthResponse response,
        ResponseCookie accessTokenCookie,
        ResponseCookie refreshTokenCookie
) {
}
