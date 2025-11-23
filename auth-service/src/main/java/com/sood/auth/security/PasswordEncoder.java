package com.sood.auth.security;

import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;

@Singleton
@Log4j2
public class PasswordEncoder {

    public String encode(final String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return BCryptPasswordEncoder.encode(rawPassword);
    }

    public boolean matches(final String rawPassword, final String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return BCryptPasswordEncoder.verify(rawPassword, encodedPassword);
    }

    private static class BCryptPasswordEncoder {

        private static final int BCRYPT_VERSION = 12;

        static String encode(final String password) {
            return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_VERSION));
        }

        static boolean verify(final String password, final String hashed) {
            try {
                return BCrypt.checkpw(password, hashed);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
