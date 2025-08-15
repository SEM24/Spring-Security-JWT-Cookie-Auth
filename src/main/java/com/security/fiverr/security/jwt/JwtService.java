package com.security.fiverr.security.jwt;

import com.security.fiverr.user.model.enitity.Role;
import com.security.fiverr.user.model.enitity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessExpirationMs}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshTokenExpirationMs;

    @Value("${jwt.accessCookieName}")
    private String accessCookieName;

    @Value("${jwt.refreshCookieName}")
    private String refreshCookieName;
    @Value("${jwt.accessCookiePath:/api}")
    private String accessCookiePath;

    @Value("${jwt.refreshCookiePath:/api/v1/auth/refresh}")
    private String refreshCookiePath;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // === Generate JWT ===
    public String generateToken(User user, long expirationMs) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key())
                .compact();
    }

    // === Generate HttpOnly Cookies ===
    public ResponseCookie generateAccessTokenCookie(User user) {
        String token = generateToken(user, accessTokenExpirationMs);
        return generateCookie(accessCookieName, token, accessCookiePath, accessTokenExpirationMs / 1000);
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return generateCookie(refreshCookieName, refreshToken, refreshCookiePath, refreshTokenExpirationMs / 1000);
    }

    // === Clean cookies ===
    public ResponseCookie clearAccessTokenCookie() {
        return deleteCookie(accessCookieName, accessCookiePath);
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return deleteCookie(refreshCookieName, refreshCookiePath);
    }

    // === Extract token from cookie ===
    public String extractAccessToken(HttpServletRequest request) {
        return getCookieValue(request, accessCookieName);
    }

    public String extractRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, refreshCookieName);
    }

    // === Token parsing/validation ===
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public Boolean isUserEnabledFromToken(String token) {
        return getClaims(token).get("enabled", Boolean.class);
    }

    public List<String> getRolesFromToken(String token) {
        return getClaims(token).get("roles", List.class);
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    // === Private helper methods ===
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // === Cookie utils ===
    private ResponseCookie generateCookie(String name, String value, String path, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path(path)
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie deleteCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path(path)
                .maxAge(0)
                .build();
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }
}