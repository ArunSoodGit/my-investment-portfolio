package com.sood.market.data.service;

import com.sood.market.data.exception.MarketDataApiException;
import com.sood.market.data.model.TwelveDataResponse;
import jakarta.inject.Singleton;

@Singleton
public class ResponseValidator {

    public void validate(final TwelveDataResponse response, final String symbol) {
        if (response == null) {
            throw new MarketDataApiException("API returned null response for symbol: " + symbol);
        }
        if (response.getSymbol() == null || response.getSymbol().isEmpty()) {
            throw new MarketDataApiException("API response missing symbol field");
        }
        if (response.getClose() == null || response.getClose().isEmpty()) {
            throw new MarketDataApiException("API response missing price data for symbol: " + symbol);
        }
    }
}
