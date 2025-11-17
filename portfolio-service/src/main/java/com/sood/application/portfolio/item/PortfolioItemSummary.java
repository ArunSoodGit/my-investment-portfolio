package com.sood.application.portfolio.item;

import java.math.BigDecimal;

/**
 * Immutable record containing calculated financial metrics for a single portfolio item.
 * Represents a stock position with current values and performance metrics.
 *
 * @param totalQuantity the number of shares held
 * @param investedValue the total amount invested in this position
 * @param totalValue the current market value of this position
 * @param profitValue the absolute profit or loss amount
 * @param profitPercentage the profit or loss as a formatted percentage
 * @param averagePurchasePrice the average purchase price per share
 */
public record PortfolioItemSummary(
        double totalQuantity,
        BigDecimal investedValue,
        BigDecimal totalValue,
        BigDecimal profitValue,
        String profitPercentage,
        BigDecimal averagePurchasePrice
) {}