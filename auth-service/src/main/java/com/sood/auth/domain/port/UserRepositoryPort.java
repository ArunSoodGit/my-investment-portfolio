package com.sood.auth.domain.port;

import com.sood.auth.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);

    User save(User user);

    void update(User user);

    boolean existsByUsername(String username);
}