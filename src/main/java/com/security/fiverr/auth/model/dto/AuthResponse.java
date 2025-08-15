package com.security.fiverr.auth.model.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record AuthResponse(
        String message,
        String email,
        Set<String> roles
) {
}

