package com.security.fiverr.security.oauth2.handler;

import com.security.fiverr.auth.model.dto.AuthResult;
import com.security.fiverr.security.oauth2.service.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2Service oAuth2Service;
    @Value("${oauth.frontend.success-url:http://localhost:3000/oauth/callback}")
    private String frontendSuccessUrl;

    @Value("${oauth.frontend.error-url:http://localhost:3000/login}")
    private String frontendErrorUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String providerId = oAuth2User.getAttribute("sub");

            log.info("OAuth2 authentication successful for email: {}", email);

            // use AuthService to handle OAuth registration/login
            AuthResult authResult = oAuth2Service.processOAuthLogin(email, name, providerId);

            // set cookies via AuthResult
            setAuthCookies(response, authResult);

            // redirect with token
            String targetUrl = buildSuccessUrl(authResult.accessTokenCookie().getValue());
            log.info("Redirecting to: {}", targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("Error in OAuth2 success handler", e);
            redirectToError(request, response, "Authentication error occurred");
        }
    }

    private void setAuthCookies(HttpServletResponse response, AuthResult authResult) {
        response.addHeader(HttpHeaders.SET_COOKIE, authResult.accessTokenCookie().toString());
        if (authResult.accessTokenCookie() != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, authResult.accessTokenCookie().toString());
        }
    }

    private String buildSuccessUrl(String token) {
        return frontendSuccessUrl + "?token=" + token;
    }

    private void redirectToError(HttpServletRequest request, HttpServletResponse response, String error) throws IOException {
        String targetUrl = frontendErrorUrl + "?error=" + URLEncoder.encode(error, "UTF-8");
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}