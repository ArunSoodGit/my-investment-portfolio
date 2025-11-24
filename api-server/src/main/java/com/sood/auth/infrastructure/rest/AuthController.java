package com.sood.auth.infrastructure.rest;

import com.sood.auth.application.AuthApplicationService;
import com.sood.auth.infrastructure.rest.dto.LoginRequestDto;
import com.sood.auth.infrastructure.rest.dto.LoginResponseDto;
import com.sood.auth.infrastructure.rest.dto.RegisterRequestDto;
import com.sood.auth.infrastructure.rest.dto.RegisterResponseDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import lombok.extern.log4j.Log4j2;

@Controller("/v1/api/auth")
@Log4j2
public class AuthController {

    private final AuthApplicationService authService;

    public AuthController(final AuthApplicationService authService) {
        this.authService = authService;
    }

    @Post("/login")
    public HttpResponse<LoginResponseDto> login(@Body final LoginRequestDto request) {
        return authService.login(request);
    }

    @Post("/register")
    public HttpResponse<RegisterResponseDto> register(@Body final RegisterRequestDto request) {
        return authService.register(request);
    }
}