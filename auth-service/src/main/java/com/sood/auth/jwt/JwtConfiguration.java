package com.sood.auth.jwt;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class JwtConfiguration {

    private final String secret;

    public JwtConfiguration(@Value("${jwt.secret}") final String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }
}
