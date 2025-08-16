package com.security.fiverr.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/protected")
public class ProtectedController {
    
    @GetMapping("/test")
    public String testEndpoint(Authentication authentication) {
        return "Hello " + authentication.getName() + "! OAuth working correctly.";
    }
}