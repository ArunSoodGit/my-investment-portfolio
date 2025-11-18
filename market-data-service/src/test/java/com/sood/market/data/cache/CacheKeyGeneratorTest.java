package com.sood.market.data.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheKeyGeneratorTest {

    private CacheKeyGenerator keyGenerator;

    @BeforeEach
    void setUp() {
        keyGenerator = new CacheKeyGenerator();
    }

    @Test
    void shouldGenerateCorrectMarketDataKey() {
        // When
        String key = keyGenerator.generateMarketDataKey("AAPL");

        // Then
        assertEquals("market:data:AAPL", key);
    }

    @Test
    void shouldConvertSymbolToUppercase() {
        // When
        String key = keyGenerator.generateMarketDataKey("aapl");

        // Then
        assertEquals("market:data:AAPL", key);
    }

    @Test
    void shouldThrowExceptionForNullSymbol() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> keyGenerator.generateMarketDataKey(null));
    }

    @Test
    void shouldThrowExceptionForEmptySymbol() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> keyGenerator.generateMarketDataKey(""));
    }

    @Test
    void shouldThrowExceptionForWhitespaceSymbol() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> keyGenerator.generateMarketDataKey("   "));
    }

    @Test
    void shouldReturnCorrectSymbolsSetKey() {
        // When
        String key = keyGenerator.getSymbolsSetKey();

        // Then
        assertEquals("market:symbols", key);
    }
}
