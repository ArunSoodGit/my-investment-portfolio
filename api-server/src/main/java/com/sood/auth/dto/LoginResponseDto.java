package com.sood.auth.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
public class LoginResponseDto {
    private String token;
    private String type = "Bearer";
    private Long expiresIn = 3600L;
    private String message;
    private boolean success;
}
