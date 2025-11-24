package com.sood.auth.application;

import com.sood.auth.application.command.LoginCommand;
import com.sood.auth.application.command.RegisterCommand;
import com.sood.auth.application.command.ValidateTokenCommand;
import com.sood.auth.application.port.AuthServicePort;
import com.sood.auth.application.result.LoginResult;
import com.sood.auth.application.result.RegisterResult;
import com.sood.auth.application.result.TokenValidationResult;
import com.sood.auth.infrastructure.rest.dto.LoginRequestDto;
import com.sood.auth.infrastructure.rest.dto.LoginResponseDto;
import com.sood.auth.infrastructure.rest.dto.RegisterRequestDto;
import com.sood.auth.infrastructure.rest.dto.RegisterResponseDto;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;

@Singleton
public class AuthApplicationService {

    private final AuthServicePort authServicePort;

    public AuthApplicationService(final AuthServicePort authServicePort) {
        this.authServicePort = authServicePort;
    }

    public HttpResponse<LoginResponseDto> login(final LoginRequestDto request) {
        final LoginCommand command = new LoginCommand(request.getUsername(), request.getPassword());
        final LoginResult result = authServicePort.login(command);
        final LoginResponseDto dto = new LoginResponseDto(result.token(), result.message(), result.success());

        if (result.success()) {
            return HttpResponse.ok(dto);
        } else {
            return HttpResponse.unauthorized();
        }
    }

    public HttpResponse<RegisterResponseDto> register(final RegisterRequestDto request) {
        final RegisterCommand command =
                new RegisterCommand(request.getUsername(), request.getPassword(), request.getEmail());
        final RegisterResult result = authServicePort.register(command);
        final RegisterResponseDto dto = new RegisterResponseDto(result.userId(), result.message(), result.success());

        if (result.success()) {
            return HttpResponse.created(dto);
        } else {
            return HttpResponse.badRequest(dto);
        }
    }

    public TokenValidationResult validateToken(final String token) {
        final ValidateTokenCommand command = new ValidateTokenCommand(token);
        return authServicePort.validateToken(command);
    }
}