package com.sood.auth.mapper;

import com.example.market.grpc.LoginRequest;
import com.example.market.grpc.RegisterRequest;
import com.sood.auth.dto.LoginRequestDto;
import com.sood.auth.dto.RegisterRequestDto;
import jakarta.inject.Singleton;

@Singleton
public class AuthRequestMapper {

    public LoginRequest mapToGrpc(final LoginRequestDto dto) {
        return LoginRequest.newBuilder()
                .setUsername(dto.getUsername())
                .setPassword(dto.getPassword())
                .build();
    }

    public RegisterRequest mapToGrpc(final RegisterRequestDto dto) {
        return RegisterRequest.newBuilder()
                .setUsername(dto.getUsername())
                .setEmail(dto.getEmail())
                .setPassword(dto.getPassword())
                .build();
    }
}
