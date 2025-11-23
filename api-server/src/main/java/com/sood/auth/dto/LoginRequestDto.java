package com.sood.auth.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class LoginRequestDto {
    private String username;
    private String password;
}
