package com.security.fiverr.security.token.service;

import com.security.fiverr.security.token.model.entity.RefreshToken;
import com.security.fiverr.user.model.enitity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenService {
    @Transactional
    RefreshToken createRefreshToken(User user);

    Optional<RefreshToken> findByToken(String token);

    boolean isRefreshTokenExpired(RefreshToken refreshToken);

    @Transactional
    RefreshToken validateAndRefresh(String tokenValue);

    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByTokenValue(String token);

    void delete(RefreshToken refreshToken);
}
