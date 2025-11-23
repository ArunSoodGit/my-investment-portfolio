package com.sood.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.util.List;

@Singleton
@Log4j2
public class JwtClaimsExtractor {

    private final SecretKey signingKey;

    public JwtClaimsExtractor(final JwtConfiguration jwtConfig) {
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public String extractUserId(final String token) {
        final Claims claims = extractClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public String extractUsername(final String token) {
        final Claims claims = extractClaims(token);
        return claims != null ? (String) claims.get("username") : null;
    }

    public List<String> extractRoles(final String token) {
        final Claims claims = extractClaims(token);
        return claims != null ? (List<String>) claims.get("roles") : List.of();
    }

    public String extractTokenType(final String token) {
        final Claims claims = extractClaims(token);
        return claims != null ? (String) claims.get("token_type") : null;
    }

    private Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            return null;
        }
    }
}
