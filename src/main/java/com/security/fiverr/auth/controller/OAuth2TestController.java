package com.security.fiverr.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2TestController {
    
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
    @GetMapping("/oauth2/test")
    public String testOAuth2Config() {
        try {
            ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
            return "Google OAuth2 configuration found: " + googleRegistration.getClientName();
        } catch (Exception e) {
            return "Google OAuth2 configuration NOT found: " + e.getMessage();
        }
    }
}