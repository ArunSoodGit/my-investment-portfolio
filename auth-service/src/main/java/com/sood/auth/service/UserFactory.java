package com.sood.auth.service;

import com.sood.auth.infrastructure.entity.UserEntity;
import com.sood.auth.security.PasswordEncoder;
import jakarta.inject.Singleton;
import java.time.LocalDateTime;

@Singleton
public class UserFactory {

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private final PasswordEncoder passwordEncoder;

    public UserFactory(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity createNewUser(final String username, final String email, final String rawPassword) {
        return UserEntity.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(DEFAULT_ROLE)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public UserEntity updateLastLogin(final UserEntity user) {
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(LocalDateTime.now())
                .build();
    }
}
