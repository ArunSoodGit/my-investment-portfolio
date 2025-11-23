package com.sood.auth.service;

import com.sood.auth.dto.LoginRequestDto;
import com.sood.auth.dto.RefreshTokenRequestDto;
import com.sood.auth.dto.RegisterRequestDto;
import jakarta.inject.Singleton;

@Singleton
public class AuthRequestValidator {

    public void validate(final LoginRequestDto request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required");
        }
    }

    public void validate(final RegisterRequestDto request) {
        if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Username, email and password are required");
        }
    }

    public void validate(final RefreshTokenRequestDto request) {
        if (request.getToken() == null || request.getToken().isBlank()) {
            throw new IllegalArgumentException("Token is required for refresh");
        }
    }
}
