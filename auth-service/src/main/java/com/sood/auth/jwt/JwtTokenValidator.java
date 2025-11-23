package com.sood.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;

@Singleton
@Log4j2
public class JwtTokenValidator {

    private final SecretKey signingKey;

    public JwtTokenValidator(final JwtConfiguration jwtConfig) {
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(final String token) {
        try {
            final var claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getExpiration() == null) {
                return true;
            }
            return claims.getExpiration().toInstant().isBefore(java.time.Instant.now());
        } catch (Exception e) {
            return true;
        }
    }
}
