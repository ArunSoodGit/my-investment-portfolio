package com.sood.auth.service;

import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RegisterResponse;
import com.example.market.grpc.TokenValidationResponse;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class AuthResponseBuilder {

    public LoginResponse buildSuccessLoginResponse(final String token, final long jwtExpiration) {
        return LoginResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Login successful")
                .setToken(token)
                .setExpiration(jwtExpiration)
                .build();
    }

    public LoginResponse buildFailedLoginResponse(final String message) {
        return LoginResponse.newBuilder()
                .setSuccess(false)
                .setMessage(message)
                .build();
    }

    public RegisterResponse buildSuccessRegisterResponse(final Long userId) {
        return RegisterResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Registration successful")
                .setUserId(String.valueOf(userId))
                .build();
    }

    public RegisterResponse buildFailedRegisterResponse(final String message) {
        return RegisterResponse.newBuilder()
                .setSuccess(false)
                .setMessage(message)
                .build();
    }

    public TokenValidationResponse buildValidTokenResponse(final String userId, final String username,
            final List<String> roles, final String tokenType) {
        return TokenValidationResponse.newBuilder()
                .setValid(true)
                .setUserId(userId)
                .setUsername(username != null ? username : "")
                .addAllRoles(roles)
                .setTokenType(tokenType != null ? tokenType : "Bearer")
                .build();
    }

    public TokenValidationResponse buildInvalidTokenResponse(final String errorMessage) {
        return TokenValidationResponse.newBuilder()
                .setValid(false)
                .setError(errorMessage)
                .build();
    }
}
