package com.sood.auth.service;

import com.sood.auth.infrastructure.entity.UserEntity;
import com.sood.auth.infrastructure.repository.UserRepository;
import com.sood.auth.security.PasswordEncoder;
import jakarta.inject.Singleton;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class UserAuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthValidator(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserEntity> validateLoginCredentials(final String username, final String password) {
        final Optional<UserEntity> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            log.warn("Login attempt for non-existent user: {}", username);
            return Optional.empty();
        }

        final UserEntity user = userOpt.get();

        if (!user.getEnabled()) {
            log.warn("Login attempt for disabled user: {}", username);
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Failed login attempt for user: {}", username);
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public boolean usernameExists(final String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(final String email) {
        return userRepository.existsByEmail(email);
    }
}
