package com.sood.auth.api;

import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RegisterResponse;
import com.example.market.grpc.TokenValidationResponse;
import com.sood.auth.application.result.LoginResult;
import com.sood.auth.application.result.RegisterResult;
import com.sood.auth.application.result.TokenValidationResult;
import jakarta.inject.Singleton;

@Singleton
public class AuthResponseBuilder {

    public LoginResponse build(final LoginResult result) {
        final LoginResponse.Builder builder = LoginResponse.newBuilder()
                .setSuccess(result.success())
                .setMessage(result.message());

        if (result.success() && result.token() != null) {
            builder.setToken(result.token());
        }

        return builder.build();
    }

    public RegisterResponse build(final RegisterResult result) {
        final RegisterResponse.Builder builder = RegisterResponse.newBuilder()
                .setSuccess(result.success())
                .setMessage(result.message());

        if (result.success() && result.userId() != null) {
            builder.setUserId(String.valueOf(result.userId()));
        }

        return builder.build();
    }

    public TokenValidationResponse build(final TokenValidationResult result) {
        final TokenValidationResponse.Builder builder = TokenValidationResponse.newBuilder()
                .setValid(result.valid())
                .setError(result.error());

        return builder.build();
    }
}