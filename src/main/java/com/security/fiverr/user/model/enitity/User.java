package com.security.fiverr.user.model.enitity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false, unique = true)
    @Size(max = 50)
    private String email;

    @Size(max = 40)
    private String username;

    @Size(max = 120)
    @Column(nullable = false)
    private String password;
    private boolean enabled = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private AuthProviderType provider = AuthProviderType.LOCAL;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "name")
    private String name;
}