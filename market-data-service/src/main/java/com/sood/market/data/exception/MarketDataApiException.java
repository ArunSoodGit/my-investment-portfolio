package com.sood.market.data.exception;

public class MarketDataApiException extends RuntimeException {
    public MarketDataApiException(final String message) {
        super(message);
    }

    public MarketDataApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
