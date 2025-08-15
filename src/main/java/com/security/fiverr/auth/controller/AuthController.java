package com.security.fiverr.auth.controller;

import com.security.fiverr.auth.model.dto.*;
import com.security.fiverr.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.email());

        AuthResult result = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(result.response());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        AuthResult result = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(result.response());
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(HttpServletRequest request) {
        log.info("Access Token refresh attempt");

        AuthResult result = authService.refreshAccessToken(request);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString());

        //Add refresh cookie only if it exists
        if (result.refreshTokenCookie() != null) {
            responseBuilder.header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString());
        }

        return responseBuilder.body(result.response());
    }


    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        log.info("Logout attempt");

        LogoutResponse response = authService.logout(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, response.refreshTokenCookie().toString())
                .body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getCurrentUser(Authentication authentication) {
        log.info("Get current user info for: {}", authentication.getName());
        // TODO: Implement method to get current user info
        return ResponseEntity.ok("Current user: " + authentication.getName());
    }
    @GetMapping("/user/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> getUser(Authentication authentication) {
        log.info("Get current user info for: {}", authentication.getName());
        // TODO: Implement method to get current user info
        return ResponseEntity.ok("Current user: " + authentication.getName());
    }
    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> validateToken(Authentication authentication) {
        log.info("Token validation for: {}", authentication.getName());
        return ResponseEntity.ok("Token is valid for user: " + authentication.getName());
    }
}
