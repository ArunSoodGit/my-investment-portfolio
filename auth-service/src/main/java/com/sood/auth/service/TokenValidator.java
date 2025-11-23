package com.sood.auth.service;

import com.sood.auth.jwt.JwtClaimsExtractor;
import com.sood.auth.jwt.JwtTokenValidator;
import jakarta.inject.Singleton;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class TokenValidator {

    private final JwtTokenValidator tokenValidator;
    private final JwtClaimsExtractor claimsExtractor;

    public TokenValidator(final JwtTokenValidator tokenValidator, final JwtClaimsExtractor claimsExtractor) {
        this.tokenValidator = tokenValidator;
        this.claimsExtractor = claimsExtractor;
    }

    public Optional<String> validateAndExtractUserId(final String token) {
        if (token == null || token.isBlank()) {
            log.warn("Token validation failed: Token is missing or empty");
            return Optional.empty();
        }

        if (!tokenValidator.validateToken(token)) {
            log.warn("Token validation failed: Invalid JWT token");
            return Optional.empty();
        }

        if (tokenValidator.isTokenExpired(token)) {
            log.warn("Token validation failed: JWT token has expired");
            return Optional.empty();
        }

        final String userId = claimsExtractor.extractUserId(token);
        if (userId == null) {
            log.warn("Token validation failed: User ID not found in token");
            return Optional.empty();
        }

        return Optional.of(userId);
    }
}
