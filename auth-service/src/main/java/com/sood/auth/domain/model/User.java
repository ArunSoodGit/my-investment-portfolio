package com.sood.auth.domain.model;

import com.sood.auth.domain.service.PasswordPolicy;
import com.sood.auth.infrastructure.entity.UserEntity;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class User {
    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final String role;
    private final boolean enabled;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public User(Long id, String username, String email, String password, String role, boolean enabled,
            LocalDateTime createdAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    public static User fromEntity(final UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getEnabled(),
                entity.getCreatedAt(),
                entity.getLastLoginAt()
        );
    }

    public boolean checkPassword(String rawPassword, PasswordPolicy passwordPolicy) {
        return passwordPolicy.validateLogin(this, rawPassword);
    }

    public void login() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public static User register(String username, String email, String password, String role) {
        return new User(null, username, email, password, role, true, LocalDateTime.now(), LocalDateTime.now());
    }
}