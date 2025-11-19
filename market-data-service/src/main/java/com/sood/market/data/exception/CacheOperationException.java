package com.sood.market.data.exception;

public class CacheOperationException extends RuntimeException {
    public CacheOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
