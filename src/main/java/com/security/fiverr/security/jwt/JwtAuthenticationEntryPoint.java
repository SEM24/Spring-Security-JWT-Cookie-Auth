package com.security.fiverr.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.error("Authentication error: {} for path: {}", authException.getMessage(), request.getServletPath());

        if (response.isCommitted()) {
            log.warn("Response already committed, cannot handle authentication error");
            return;
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        int status;
        String error;
        String message;

        if (authException instanceof BadCredentialsException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error = "Invalid credentials";
            message = "Invalid username or password";
        } else if (authException instanceof InsufficientAuthenticationException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error = "Authentication required";
            message = "Full authentication is required to access this resource";
        } else {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error = "Unauthorized";
            message = "Authentication required";
        }

        response.setStatus(status);

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status,
                "error", error,
                "message", message,
                "path", request.getServletPath()
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}