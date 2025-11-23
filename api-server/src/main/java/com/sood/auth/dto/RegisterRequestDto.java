package com.sood.auth.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class RegisterRequestDto {
    private String username;
    private String email;
    private String password;
}
