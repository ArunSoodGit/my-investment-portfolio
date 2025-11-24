package com.sood.auth.application.result;

public record TokenValidationResult(boolean valid, String userId, String error) {

    public static TokenValidationResult success(String userId) {
        return new TokenValidationResult(true, userId, "");
    }

    public static TokenValidationResult failure(String error) {
        return new TokenValidationResult(false, "", error == null ? "" : error);
    }
}