//package com.security.fiverr.security.oauth2.handler;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
//
//    @Value("${app.oauth2.authorized-redirect-uris}")
//    private String redirectUri;
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request,
//                                      HttpServletResponse response,
//                                      AuthenticationException exception) throws IOException {
//
//        log.error("OAuth2 authentication failed: {}", exception.getMessage());
//
//        getRedirectStrategy().sendRedirect(request, response,
//            redirectUri + "?error=oauth2_authentication_failed");
//    }
//}