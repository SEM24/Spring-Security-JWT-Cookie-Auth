package com.security.fiverr.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.error("Access denied error: {} for path: {}", accessDeniedException.getMessage(), request.getServletPath());

        if (response.isCommitted()) {
            log.warn("Response already committed, cannot handle access denied error");
            return;
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpServletResponse.SC_FORBIDDEN,
                "error", "Access Denied",
                "message", "You don't have permission to access this resource",
                "path", request.getServletPath()
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}