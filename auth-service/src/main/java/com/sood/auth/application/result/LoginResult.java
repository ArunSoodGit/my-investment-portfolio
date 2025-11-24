package com.sood.auth.application.result;

public record LoginResult(boolean success, String message, String token) {}
