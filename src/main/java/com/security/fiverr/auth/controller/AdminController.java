package com.security.fiverr.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminController {
    
    @GetMapping("/users")
    public ResponseEntity<String> getAllUsers(Authentication authentication) {
        log.info("Admin {} requested all users", authentication.getName());
        // TODO: Implement logic to get all users
        return ResponseEntity.ok("List of all users");
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId, Authentication authentication) {
        log.info("Admin {} requested to delete user {}", authentication.getName(), userId);
        // TODO: Implement logic to delete user
        return ResponseEntity.ok("User " + userId + " deleted successfully");
    }
    
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<String> banUser(@PathVariable Long userId, Authentication authentication) {
        log.info("Admin {} requested to ban user {}", authentication.getName(), userId);
        // TODO: Implement logic to ban user
        return ResponseEntity.ok("User " + userId + " banned successfully");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<String> getSystemStats(Authentication authentication) {
        log.info("Admin {} requested system stats", authentication.getName());
        // TODO: Implement logic to get system statistics
        return ResponseEntity.ok("System statistics");
    }
}