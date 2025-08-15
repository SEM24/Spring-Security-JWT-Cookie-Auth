package com.security.fiverr.exception.dto;

import lombok.Builder;

@Builder
public record ErrorMessage(
        String message,
        String field,
        Object invalidValue
) {
}