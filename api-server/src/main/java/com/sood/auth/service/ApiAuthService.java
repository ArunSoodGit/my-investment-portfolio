package com.sood.auth.service;

import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RegisterResponse;
import com.sood.auth.dto.LoginRequestDto;
import com.sood.auth.dto.LoginResponseDto;
import com.sood.auth.dto.RefreshTokenRequestDto;
import com.sood.auth.dto.RegisterRequestDto;
import com.sood.auth.dto.RegisterResponseDto;
import com.sood.auth.grpc.AuthGrpcClient;
import com.sood.auth.mapper.AuthRequestMapper;
import com.sood.auth.mapper.AuthResponseMapper;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class ApiAuthService {

    private final AuthGrpcClient authGrpcClient;
    private final AuthRequestValidator validator;
    private final AuthRequestMapper requestMapper;
    private final AuthResponseMapper responseMapper;

    public ApiAuthService(final AuthGrpcClient authGrpcClient, final AuthRequestValidator validator,
            final AuthRequestMapper requestMapper, final AuthResponseMapper responseMapper) {
        this.authGrpcClient = authGrpcClient;
        this.validator = validator;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    public LoginResponseDto login(final LoginRequestDto request) {
        try {
            validator.validate(request);
            final LoginResponse response = authGrpcClient.login(requestMapper.mapToGrpc(request));
            return responseMapper.mapFromGrpc(response);
        } catch (final IllegalArgumentException exception) {
            return responseMapper.mapLoginError(exception.getMessage());
        } catch (final Exception exception) {
            log.error("Error during login for user: {}", request.getUsername(), exception);
            return responseMapper.mapLoginError("Login failed: " + exception.getMessage());
        }
    }

    public RegisterResponseDto register(final RegisterRequestDto request) {
        log.debug("Processing registration request for user: {}", request.getUsername());

        try {
            validator.validate(request);
            final RegisterResponse response = authGrpcClient.register(requestMapper.mapToGrpc(request));
            return responseMapper.mapFromGrpc(response);
        } catch (final IllegalArgumentException exception) {
            return responseMapper.mapRegisterError(exception.getMessage());
        } catch (final Exception exception) {
            log.error("Error during registration for user: {}", request.getUsername(), exception);
            return responseMapper.mapRegisterError("Registration failed: " + exception.getMessage());
        }
    }

    public LoginResponseDto refreshToken(final RefreshTokenRequestDto request) {
        log.debug("Processing token refresh request");
        try {
            validator.validate(request);
            final LoginResponse response = authGrpcClient.refreshToken(requestMapper.mapToGrpc(request));
            return responseMapper.mapFromGrpc(response);
        } catch (final IllegalArgumentException exception) {
            return responseMapper.mapLoginError(exception.getMessage());
        } catch (final Exception exception) {
            log.error("Error during token refresh", exception);
            return responseMapper.mapLoginError("Token refresh failed: " + exception.getMessage());
        }
    }
}
