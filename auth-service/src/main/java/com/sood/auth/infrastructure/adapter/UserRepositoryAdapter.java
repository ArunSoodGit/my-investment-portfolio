package com.sood.auth.infrastructure.adapter;


import com.sood.auth.domain.model.User;
import com.sood.auth.domain.port.UserRepositoryPort;
import com.sood.auth.infrastructure.entity.UserEntity;
import com.sood.auth.infrastructure.repository.UserRepository;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository jpaRepository;

    public UserRepositoryAdapter(final UserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return jpaRepository.findByUsername(username)
                .map(User::fromEntity);
    }

    @Override
    public User save(final User user) {
        final UserEntity entity = UserEntity.fromDomain(user);
        final UserEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void update(final User user) {
        final UserEntity entity = jpaRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setLastLoginAt(user.getLastLoginAt());
        jpaRepository.update(entity);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return jpaRepository.existsByUsername(username);
    }
}
