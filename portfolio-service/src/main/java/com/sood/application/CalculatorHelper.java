package com.sood.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import sood.found.TransactionCreatedEvent;

/**
 * Utility class for financial calculations.
 * Provides methods for calculating portfolio and item metrics with proper rounding.
 */
public final class CalculatorHelper {

    private static final String ZERO_PERCENTAGE_VALUE = "0.00%";
    private static final int PERCENTAGE_SCALE = 2;

    public static BigDecimal calculateTotalValue(final BigDecimal unitPrice, final double quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateProfitValue(final BigDecimal currentValue, final BigDecimal investedValue) {
        return currentValue.subtract(investedValue).setScale(2, RoundingMode.HALF_UP);
    }

    public static String calculateProfitPercentage(final BigDecimal profitValue, final BigDecimal investedValue) {
        if (investedValue.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO_PERCENTAGE_VALUE;
        }
        final BigDecimal percentage = profitValue.multiply(BigDecimal.valueOf(100))
                .divide(investedValue, PERCENTAGE_SCALE, RoundingMode.HALF_UP);
        return String.format("%.2f%%", percentage);
    }

    public static BigDecimal calculateTransactionValue(final TransactionCreatedEvent event) {
        return event.price().multiply(BigDecimal.valueOf(event.quantity()));
    }
}