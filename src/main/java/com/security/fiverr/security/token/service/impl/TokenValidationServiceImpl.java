package com.security.fiverr.security.token.service.impl;

import com.security.fiverr.exception.GlobalServiceException;
import com.security.fiverr.security.jwt.JwtService;
import com.security.fiverr.security.token.service.TokenValidationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationServiceImpl implements TokenValidationService {
    private final JwtService jwtService;

    @Override
    public String extractRefreshTokenFromRequest(HttpServletRequest request) {
        String refreshTokenValue = jwtService.extractRefreshToken(request);

        if (refreshTokenValue == null) {
            throw new GlobalServiceException(HttpStatus.BAD_REQUEST,"Refresh token not found in cookies");
        }

        return refreshTokenValue;
    }

    @Override
    public void validateRefreshTokenPresence(String token) {
        if (token == null) {
            throw new GlobalServiceException(HttpStatus.BAD_REQUEST,"Refresh token not found in cookies");
        }
    }

}
