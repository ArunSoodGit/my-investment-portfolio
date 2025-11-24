package com.sood.auth.infrastructure;

import com.sood.auth.domain.model.User;
import com.sood.auth.domain.service.PasswordPolicy;
import jakarta.inject.Singleton;
import org.mindrot.jbcrypt.BCrypt;

@Singleton
public class BcryptPasswordPolicy implements PasswordPolicy {

    @Override
    public boolean validateLogin(final User user, final String password) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, user.getPassword());
        } catch (final IllegalArgumentException exception) {
            return false;
        }
    }


    @Override
    public void validate(final String password) {
        if (password.length() < 6) throw new IllegalArgumentException("Password too short");
    }

    public String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}