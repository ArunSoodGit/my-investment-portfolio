package com.sood.auth.api;

import com.example.market.grpc.AuthServiceGrpc;
import com.example.market.grpc.LoginRequest;
import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RegisterRequest;
import com.example.market.grpc.RegisterResponse;
import com.example.market.grpc.TokenValidationRequest;
import com.example.market.grpc.TokenValidationResponse;
import com.sood.auth.application.AuthApplicationService;
import com.sood.auth.application.command.LoginCommand;
import com.sood.auth.application.command.RegisterCommand;
import com.sood.auth.application.command.ValidateTokenCommand;
import com.sood.auth.application.result.LoginResult;
import com.sood.auth.application.result.RegisterResult;
import com.sood.auth.application.result.TokenValidationResult;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Singleton;

@GrpcService
@Singleton
public class AuthController extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthApplicationService applicationService;
    private final AuthResponseBuilder responseBuilder;

    public AuthController(final AuthApplicationService applicationService, final AuthResponseBuilder responseBuilder) {
        this.applicationService = applicationService;
        this.responseBuilder = responseBuilder;
    }

    @Override
    public void login(final LoginRequest request, final StreamObserver<LoginResponse> responseObserver) {
        final LoginCommand command = new LoginCommand(request.getUsername(), request.getPassword());
        final LoginResult result = applicationService.login(command);
        responseObserver.onNext(responseBuilder.build(result));
        responseObserver.onCompleted();
    }

    @Override
    public void register(final RegisterRequest request, final StreamObserver<RegisterResponse> responseObserver) {
        final RegisterCommand command = new RegisterCommand(request.getUsername(), request.getEmail(), request.getPassword());
        final RegisterResult result = applicationService.register(command);
        responseObserver.onNext(responseBuilder.build(result));
        responseObserver.onCompleted();
    }

    @Override
    public void validateToken(final TokenValidationRequest request, final StreamObserver<TokenValidationResponse> responseObserver) {
        final ValidateTokenCommand command = new ValidateTokenCommand(request.getToken());
        final TokenValidationResult result = applicationService.validateToken(command);
        responseObserver.onNext(responseBuilder.build(result));
        responseObserver.onCompleted();
    }
}