package com.sood.auth.infrastructure.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class JwtTokenGenerator {

    private final long jwtExpiration;
    private final SecretKey signingKey;

    public JwtTokenGenerator(final JwtConfiguration jwtConfig,
            @Value("${jwt.expiration}") final long jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public String generateToken(final Long userId, final String username, final List<String> roles) {
        final Instant now = Instant.now();
        final Instant expiryDate = now.plusMillis(jwtExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("roles", roles)
                .claim("token_type", "Bearer")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(signingKey)
                .compact();
    }
}
