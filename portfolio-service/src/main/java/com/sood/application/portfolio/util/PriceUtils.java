package com.sood.application.portfolio.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {

    public static BigDecimal parsePrice(final String price) {
        try {
            return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static String format(final BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
