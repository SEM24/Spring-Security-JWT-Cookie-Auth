package com.security.fiverr.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint(Authentication authentication) {
        String currentUser = authentication.getName();
        String authorities = authentication.getAuthorities().toString();
        
        Map<String, Object> response = Map.of(
            "message", "Access granted!",
            "user", currentUser,
            "authorities", authorities,
            "timestamp", LocalDateTime.now()
        );
        
        log.info("Protected endpoint accessed by: {}", currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "This is public endpoint",
            "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/user-info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userInfo(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "principal", authentication.getPrincipal(),
            "name", authentication.getName(),
            "authorities", authentication.getAuthorities()
        ));
    }
}