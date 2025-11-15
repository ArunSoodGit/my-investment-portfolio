package com.sood.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculatorHelper {

    private static final String PERCENTAGE_SIGN = "%";
    private static final String ZERO_PERCENTAGE_VALUE = "0%";

    private CalculatorHelper() {
    }

    public static BigDecimal calculateTotalValue(final BigDecimal unitPrice, final double quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateInvestedValue(final BigDecimal unitPrice, final double quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateProfitValue(final BigDecimal currentValue, final BigDecimal investedValue) {
        return currentValue.subtract(investedValue).setScale(2, RoundingMode.HALF_UP);
    }

    public static String calculateProfitPercentage(final BigDecimal profitValue, final BigDecimal investedValue) {
        if (investedValue.compareTo(BigDecimal.ZERO) == 0) return ZERO_PERCENTAGE_VALUE;
        return profitValue.multiply(BigDecimal.valueOf(100))
                .divide(investedValue, 2, RoundingMode.HALF_UP) + PERCENTAGE_SIGN;
    }
}