package com.sood.auth.domain.port;


import java.util.List;

public interface TokenServicePort {
    String generateToken(Long userId, String username, List<String> roles);

    boolean validateToken(String token);
}