package com.sood.auth.infrastructure.grpc;

import com.example.market.grpc.AuthServiceGrpc;
import com.example.market.grpc.LoginRequest;
import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RegisterRequest;
import com.example.market.grpc.RegisterResponse;
import com.example.market.grpc.TokenValidationRequest;
import com.example.market.grpc.TokenValidationResponse;
import com.sood.auth.application.command.LoginCommand;
import com.sood.auth.application.command.RegisterCommand;
import com.sood.auth.application.command.ValidateTokenCommand;
import com.sood.auth.application.port.AuthServicePort;
import com.sood.auth.application.result.LoginResult;
import com.sood.auth.application.result.RegisterResult;
import com.sood.auth.application.result.TokenValidationResult;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class AuthGrpcClientAdapter implements AuthServicePort {

    private final AuthServiceGrpc.AuthServiceBlockingStub stub;

    public AuthGrpcClientAdapter(@GrpcChannel("auth") final io.grpc.Channel channel) {
        this.stub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public LoginResult login(final LoginCommand command) {
        try {
            final LoginRequest request = LoginRequest.newBuilder()
                    .setUsername(command.username())
                    .setPassword(command.password())
                    .build();

            log.debug("Sending gRPC login request for {}", command.username());
            LoginResponse response = stub.login(request);

            return new LoginResult(response.getSuccess(), response.getToken(), response.getMessage());
        } catch (Exception e) {
            log.error("gRPC login error for {}: {}", command.username(), e.getMessage());
            return new LoginResult(false, "", "Auth service unavailable: " + e.getMessage());
        }
    }

    @Override
    public RegisterResult register(final RegisterCommand command) {
        try {
            final RegisterRequest request = RegisterRequest.newBuilder()
                    .setUsername(command.username())
                    .setPassword(command.password())
                    .setEmail(command.email())
                    .build();

            log.debug("Sending gRPC register request for {}", command.username());
            final RegisterResponse response = stub.register(request);

            return new RegisterResult(response.getSuccess(), response.getUserId(), response.getMessage());
        } catch (Exception e) {
            log.error("gRPC register error for {}: {}", command.username(), e.getMessage());
            return new RegisterResult(false, "", "Auth service unavailable: " + e.getMessage());
        }
    }

    @Override
    public TokenValidationResult validateToken(final ValidateTokenCommand command) {
        try {
            final TokenValidationRequest request = TokenValidationRequest.newBuilder()
                    .setToken(command.token())
                    .build();

            final TokenValidationResponse response = stub.validateToken(request);
            return new TokenValidationResult(response.getValid(), response.getUserId(), response.getError());

        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return new TokenValidationResult(false, "", "Auth service unavailable");
        }
    }
}