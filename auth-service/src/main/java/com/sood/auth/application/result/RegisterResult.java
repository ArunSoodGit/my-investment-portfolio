package com.sood.auth.application.result;


public record RegisterResult(boolean success, String message, Long userId) {}