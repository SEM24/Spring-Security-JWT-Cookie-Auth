package com.security.fiverr.security.token.model.entity;

import com.security.fiverr.user.model.enitity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    // The user can be logged in on multiple devices, the session is independent.
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    private boolean revoked;
}
