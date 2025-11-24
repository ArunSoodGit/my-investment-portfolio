package com.sood.auth.application.result;

public record TokenValidationResult(boolean valid, String error) {
}