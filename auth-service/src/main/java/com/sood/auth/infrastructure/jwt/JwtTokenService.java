package com.sood.auth.infrastructure.jwt;

import com.sood.auth.domain.port.TokenServicePort;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class JwtTokenService implements TokenServicePort {

    private final JwtTokenGenerator tokenGenerator;
    private final JwtTokenValidator tokenValidator;

    public JwtTokenService(final JwtTokenGenerator tokenGenerator, final JwtTokenValidator tokenValidator) {
        this.tokenGenerator = tokenGenerator;
        this.tokenValidator = tokenValidator;
    }

    @Override
    public String generateToken(final Long userId, final String username, final List<String> roles) {
        return tokenGenerator.generateToken(userId, username, roles);
    }

    @Override
    public boolean validateToken(final String token) {
        return tokenValidator.validateToken(token);
    }
}
