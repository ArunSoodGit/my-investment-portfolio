package com.sood.application.portfolio;

import java.math.BigDecimal;

/**
 * Immutable record containing aggregated portfolio financial metrics.
 * Represents summary data for a complete portfolio including current value, invested amount, and profit information.
 *
 * @param currentValue the current market value of all positions
 * @param investedValue the total amount invested
 * @param profitValue the absolute profit or loss value
 * @param profitPercentage the profit or loss as a formatted percentage
 */
public record PortfolioSummary(
        BigDecimal currentValue,
        BigDecimal investedValue,
        BigDecimal profitValue,
        String profitPercentage
) {
}