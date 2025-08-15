//package com.security.fiverr.auth.controller;
//
//import com.security.fiverr.auth.model.dto.AuthResponse;
//import com.security.fiverr.security.oauth2.service.OAuth2AuthService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//import java.util.Set;
//
//@RestController
//@RequestMapping("/v1/auth")
//@RequiredArgsConstructor
//@Slf4j
//@Validated
//public class OAuth2Controller {
//    private final OAuth2AuthService oAuth2AuthService;
//
//    @GetMapping("/oauth2/providers")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<Set<String>> getLinkedProviders(Authentication authentication) {
//        Set<String> providers = oAuth2AuthService.getLinkedProviders(authentication.getName());
//        return ResponseEntity.ok(providers);
//    }
//
//    @DeleteMapping("/oauth2/unlink/{provider}")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<String> unlinkProvider(@PathVariable String provider, Authentication authentication) {
//        oAuth2AuthService.unlinkOAuth2Provider(authentication.getName(), provider);
//        return ResponseEntity.ok("Provider " + provider + " unlinked successfully");
//    }
//}
