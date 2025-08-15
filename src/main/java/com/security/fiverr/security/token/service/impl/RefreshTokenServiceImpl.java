package com.security.fiverr.security.token.service.impl;

import com.security.fiverr.exception.GlobalServiceException;
import com.security.fiverr.security.token.model.entity.RefreshToken;
import com.security.fiverr.security.token.repository.RefreshTokenRepository;
import com.security.fiverr.security.token.service.RefreshTokenService;
import com.security.fiverr.user.model.enitity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refreshExpirationMs}")
    private long refreshTokenExpirationMs;

    @Transactional
    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public boolean isRefreshTokenExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }

    @Transactional
    @Override
    public RefreshToken validateAndRefresh(String tokenValue) {
        RefreshToken refreshToken = findByToken(tokenValue)
                .orElseThrow(() -> new GlobalServiceException(HttpStatus.BAD_REQUEST, "Invalid refresh token"));

        if (isRefreshTokenExpired(refreshToken)) {
            refreshTokenRepository.delete(refreshToken);
            throw new GlobalServiceException(HttpStatus.BAD_REQUEST,"Refresh token expired");
        }

        return refreshToken;
    }

    @Transactional
    @Override
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    @Override
    public void deleteByTokenValue(String token) {
        findByToken(token).ifPresent(this::delete);
    }

    @Override
    @Transactional
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
