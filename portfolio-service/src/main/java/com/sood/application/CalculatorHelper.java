package com.sood.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for financial calculations.
 * Provides methods for calculating portfolio and item metrics with proper rounding.
 */
public final class CalculatorHelper {

    private static final String ZERO_PERCENTAGE_VALUE = "0.00%";
    private static final int PERCENTAGE_SCALE = 2;

    private CalculatorHelper() {
    }

    /**
     * Calculates total value of a position.
     * Multiplies unit price by quantity with proper rounding.
     *
     * @param unitPrice the price per unit
     * @param quantity the number of units
     * @return total value rounded to 2 decimal places
     */
    public static BigDecimal calculateTotalValue(final BigDecimal unitPrice, final double quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates profit or loss value.
     * Subtracts invested value from current value.
     *
     * @param currentValue the current market value
     * @param investedValue the original invested amount
     * @return profit value (positive for gains, negative for losses)
     */
    public static BigDecimal calculateProfitValue(final BigDecimal currentValue, final BigDecimal investedValue) {
        return currentValue.subtract(investedValue).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates profit percentage.
     * Returns formatted percentage string or "0.00%" if no investment.
     *
     * @param profitValue the profit value
     * @param investedValue the original invested amount
     * @return formatted percentage string (e.g., "15.50%")
     */
    public static String calculateProfitPercentage(final BigDecimal profitValue, final BigDecimal investedValue) {
        if (investedValue.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO_PERCENTAGE_VALUE;
        }
        final BigDecimal percentage = profitValue.multiply(BigDecimal.valueOf(100))
                .divide(investedValue, PERCENTAGE_SCALE, RoundingMode.HALF_UP);
        return String.format("%.2f%%", percentage);
    }
}