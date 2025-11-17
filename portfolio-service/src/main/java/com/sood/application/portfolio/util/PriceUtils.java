package com.sood.application.portfolio.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for price parsing and formatting operations.
 * Handles conversion between string prices and BigDecimal with proper rounding.
 */
public class PriceUtils {

    private PriceUtils() {
    }

    /**
     * Parses a price string into a BigDecimal with 2 decimal places.
     * Returns BigDecimal.ZERO if parsing fails.
     *
     * @param price the price string to parse
     * @return parsed price or ZERO if invalid
     */
    public static BigDecimal parsePrice(final String price) {
        try {
            return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Formats a BigDecimal price value to a string with 2 decimal places.
     *
     * @param value the value to format
     * @return formatted price string
     */
    public static String format(final BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
