package com.sood.market.data.cache;

import jakarta.inject.Singleton;

@Singleton
public class CacheKeyGenerator {

    private static final String MARKET_DATA_PREFIX = "market:data:";
    private static final String SYMBOLS_SET_KEY = "market:symbols";

    public String generateMarketDataKey(final String symbol) {
        validateSymbol(symbol);
        return MARKET_DATA_PREFIX + symbol.toUpperCase();
    }

    public String getSymbolsSetKey() {
        return SYMBOLS_SET_KEY;
    }

    private void validateSymbol(final String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
    }
}
