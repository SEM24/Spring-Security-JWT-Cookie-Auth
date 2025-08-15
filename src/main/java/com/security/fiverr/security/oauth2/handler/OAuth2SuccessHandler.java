//package com.security.fiverr.security.oauth2.handler;
//
//import com.security.fiverr.auth.model.dto.AuthResult;
//import com.security.fiverr.security.oauth2.service.OAuth2AuthService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final OAuth2AuthService oAuth2AuthService;
//
//    @Value("${app.oauth2.authorized-redirect-uris}")
//    private String redirectUri;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                      HttpServletResponse response,
//                                      Authentication authentication) throws IOException {
//
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//        try {
//            AuthResult result = oAuth2AuthService.processOAuth2Login(oAuth2User);
//
//            response.addHeader(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString());
//            response.addHeader(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString());
//
//            // Redirect to front
//            getRedirectStrategy().sendRedirect(request, response, redirectUri + "?success=true");
//
//        } catch (Exception e) {
//            log.error("OAuth2 authentication failed", e);
//            getRedirectStrategy().sendRedirect(request, response, redirectUri + "?error=true");
//        }
//    }
//}