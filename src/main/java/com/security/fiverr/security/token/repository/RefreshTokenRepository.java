package com.security.fiverr.security.token.repository;

import com.security.fiverr.security.token.model.entity.RefreshToken;
import com.security.fiverr.user.model.enitity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}