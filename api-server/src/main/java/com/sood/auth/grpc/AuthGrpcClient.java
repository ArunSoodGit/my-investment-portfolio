package com.sood.auth.grpc;

import com.example.market.grpc.AuthServiceGrpc;
import com.example.market.grpc.LoginRequest;
import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RefreshTokenRequest;
import com.example.market.grpc.RegisterRequest;
import com.example.market.grpc.RegisterResponse;
import com.example.market.grpc.TokenValidationRequest;
import com.example.market.grpc.TokenValidationResponse;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class AuthGrpcClient {

    private final AuthServiceGrpc.AuthServiceBlockingStub authStub;

    public AuthGrpcClient(@GrpcChannel("auth") final io.grpc.Channel channel) {
        this.authStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    public TokenValidationResponse validateToken(final String token) {
        try {
            final TokenValidationRequest request = TokenValidationRequest.newBuilder()
                    .setToken(token)
                    .build();

            log.debug("Sending token validation request to auth-service");
            final TokenValidationResponse response = authStub.validateToken(request);

            if (response.getValid()) {
                log.debug("Token validation successful for user: {}", response.getUserId());
            } else {
                log.warn("Token validation failed: {}", response.getError());
            }

            return response;
        } catch (Exception e) {
            log.error("Error communicating with auth-service: {}", e.getMessage(), e);
            return TokenValidationResponse.newBuilder()
                    .setValid(false)
                    .setError("Auth service unavailable: " + e.getMessage())
                    .build();
        }
    }

    public LoginResponse login(final LoginRequest request) {
        try {
            log.debug("Sending login request to auth-service for user: {}", request.getUsername());
            final LoginResponse response = authStub.login(request);

            if (response.getSuccess()) {
                log.info("Login successful for user: {}", request.getUsername());
            } else {
                log.warn("Login failed for user: {} - {}", request.getUsername(), response.getMessage());
            }

            return response;
        } catch (Exception e) {
            log.error("Error communicating with auth-service during login: {}", e.getMessage(), e);
            return LoginResponse.newBuilder()
                    .setToken("")
                    .setMessage("Auth service unavailable: " + e.getMessage())
                    .setSuccess(false)
                    .build();
        }
    }

    public RegisterResponse register(final RegisterRequest request) {
        try {
            log.debug("Sending register request to auth-service for user: {}", request.getUsername());
            final RegisterResponse response = authStub.register(request);

            if (response.getSuccess()) {
                log.info("Registration successful for user: {}", request.getUsername());
            } else {
                log.warn("Registration failed for user: {} - {}", request.getUsername(), response.getMessage());
            }

            return response;
        } catch (Exception e) {
            log.error("Error communicating with auth-service during registration: {}", e.getMessage(), e);
            return RegisterResponse.newBuilder()
                    .setUserId("")
                    .setMessage("Auth service unavailable: " + e.getMessage())
                    .setSuccess(false)
                    .build();
        }
    }

    public LoginResponse refreshToken(final RefreshTokenRequest request) {
        try {
            log.debug("Sending token refresh request to auth-service");
            final LoginResponse response = authStub.refreshToken(request);

            if (response.getSuccess()) {
                log.info("Token refresh successful");
            } else {
                log.warn("Token refresh failed - {}", response.getMessage());
            }

            return response;
        } catch (Exception e) {
            log.error("Error communicating with auth-service during token refresh: {}", e.getMessage(), e);
            return LoginResponse.newBuilder()
                    .setToken("")
                    .setMessage("Auth service unavailable: " + e.getMessage())
                    .setSuccess(false)
                    .build();
        }
    }
}
