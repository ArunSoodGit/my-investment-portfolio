package com.sood.market.data.cache;

import jakarta.inject.Singleton;

/**
 * Generates cache keys for market data.
 * Centralizes key naming strategy following Single Responsibility Principle.
 */
@Singleton
public class CacheKeyGenerator {

    private static final String MARKET_DATA_PREFIX = "market:data:";
    private static final String SYMBOLS_SET_KEY = "market:symbols";

    /**
     * Generates cache key for market data of a specific symbol.
     *
     * @param symbol the stock symbol (e.g., "AAPL")
     * @return cache key (e.g., "market:data:AAPL")
     */
    public String generateMarketDataKey(final String symbol) {
        validateSymbol(symbol);
        return MARKET_DATA_PREFIX + symbol.toUpperCase();
    }

    /**
     * Returns the Redis Set key that stores all tracked symbols.
     *
     * @return the symbols set key
     */
    public String getSymbolsSetKey() {
        return SYMBOLS_SET_KEY;
    }

    /**
     * Validates that symbol is not null or empty.
     *
     * @param symbol the symbol to validate
     * @throws IllegalArgumentException if symbol is invalid
     */
    private void validateSymbol(final String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
    }
}
