package com.sood.market.data.exception;

public class MarketDataNotFoundException extends RuntimeException {
    public MarketDataNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
