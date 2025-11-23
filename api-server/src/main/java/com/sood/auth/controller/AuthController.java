package com.sood.auth.controller;

import com.sood.auth.dto.LoginRequestDto;
import com.sood.auth.dto.LoginResponseDto;
import com.sood.auth.dto.RegisterRequestDto;
import com.sood.auth.dto.RegisterResponseDto;
import com.sood.auth.service.ApiAuthService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import lombok.extern.log4j.Log4j2;

@Controller("/v1/api/auth")
@Log4j2
public class AuthController {

    private final ApiAuthService authService;

    public AuthController(final ApiAuthService authService) {
        this.authService = authService;
    }

    @Post("/login")
    public HttpResponse<LoginResponseDto> login(@Body final LoginRequestDto request) {
        log.debug("Received login request");
        final LoginResponseDto response = authService.login(request);

        if (response.isSuccess()) {
            return HttpResponse.ok(response);
        } else {
            return HttpResponse.unauthorized();
        }
    }

    @Post("/register")
    public HttpResponse<RegisterResponseDto> register(@Body final RegisterRequestDto request) {
        log.debug("Received registration request");
        final RegisterResponseDto response = authService.register(request);

        if (response.isSuccess()) {
            return HttpResponse.created(response);
        } else {
            return HttpResponse.badRequest(response);
        }
    }
}
