package com.sood.auth.api;

import com.example.market.grpc.AuthServiceGrpc;
import com.example.market.grpc.LoginRequest;
import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RefreshTokenRequest;
import com.example.market.grpc.RegisterRequest;
import com.example.market.grpc.RegisterResponse;
import com.example.market.grpc.TokenValidationRequest;
import com.example.market.grpc.TokenValidationResponse;
import com.sood.auth.service.AuthService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import lombok.extern.log4j.Log4j2;

@GrpcService
@Log4j2
public class AuthController extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void validateToken(final TokenValidationRequest request,
            final StreamObserver<TokenValidationResponse> responseObserver) {
        try {
            log.debug("Received token validation request");

            final TokenValidationResponse response = authService.validateToken(request.getToken());

            if (response.getValid()) {
                log.info("Token validation successful for user: {}", response.getUserId());
            } else {
                log.warn("Token validation failed: {}", response.getError());
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during token validation: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void login(final LoginRequest request, final StreamObserver<LoginResponse> responseObserver) {
        try {
            final LoginResponse authResponse = authService.login(request);
            responseObserver.onNext(authResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Login failed: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void register(final RegisterRequest request, final StreamObserver<RegisterResponse> responseObserver) {
        try {
            final RegisterResponse authResponse = authService.register(request);
            responseObserver.onNext(authResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Registration failed: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void refreshToken(final RefreshTokenRequest request, final StreamObserver<LoginResponse> responseObserver) {
        try {
            final TokenValidationResponse validation = authService.refreshTokenIfValid(request.getToken());
            final LoginResponse loginResponse = LoginResponse.newBuilder()
                    .setToken(request.getToken())
                    .setMessage(validation.getError())
                    .setSuccess(validation.getValid())
                    .build();

            responseObserver.onNext(loginResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Token refresh failed: " + e.getMessage())
                    .asException());
        }
    }
}
