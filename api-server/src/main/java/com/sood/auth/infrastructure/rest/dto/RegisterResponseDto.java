package com.sood.auth.infrastructure.rest.dto;

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
public class RegisterResponseDto {
    private String userId;
    private String message;
    private boolean success;
}
