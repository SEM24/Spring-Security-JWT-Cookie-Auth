package com.security.fiverr.auth.model.dto;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder
public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password
) {
}