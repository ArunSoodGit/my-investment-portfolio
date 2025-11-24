package com.sood.auth.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class JwtTokenValidator {

    private final SecretKey signingKey;

    public JwtTokenValidator(final JwtConfiguration jwtConfig) {
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public boolean validateToken(final String token) {
        try {
            final var claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return isNotExpired(claims);
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isNotExpired(final Claims claims) {
        final var expiration = claims.getExpiration();
        return expiration != null && expiration.after(Date.from(Instant.now()));
    }
}