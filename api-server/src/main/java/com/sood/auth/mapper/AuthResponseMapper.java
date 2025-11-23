package com.sood.auth.mapper;

import com.example.market.grpc.LoginResponse;
import com.example.market.grpc.RegisterResponse;
import com.sood.auth.dto.LoginResponseDto;
import com.sood.auth.dto.RegisterResponseDto;
import jakarta.inject.Singleton;

@Singleton
public class AuthResponseMapper {

    public LoginResponseDto mapFromGrpc(final LoginResponse response) {
        return LoginResponseDto.builder()
                .token(response.getSuccess() ? response.getToken() : null)
                .type("Bearer")
                .expiresIn(response.getExpiration())
                .message(response.getMessage())
                .success(response.getSuccess())
                .build();
    }

    public LoginResponseDto mapLoginError(final String errorMessage) {
        return LoginResponseDto.builder()
                .token(null)
                .type("Bearer")
                .message(errorMessage)
                .success(false)
                .build();
    }

    public RegisterResponseDto mapFromGrpc(final RegisterResponse response) {
        return RegisterResponseDto.builder()
                .userId(response.getSuccess() ? response.getUserId() : null)
                .message(response.getMessage())
                .success(response.getSuccess())
                .build();
    }

    public RegisterResponseDto mapRegisterError(final String errorMessage) {
        return RegisterResponseDto.builder()
                .userId(null)
                .message(errorMessage)
                .success(false)
                .build();
    }
}
