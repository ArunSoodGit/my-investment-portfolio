package com.sood.application.portfolio.item;

import com.sood.application.CalculatorHelper;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates individual portfolio item metrics.
 * Computes financial data for a single stock position including profit/loss.
 */
@Singleton
public class PortfolioItemCalculator {

    /**
     * Calculates financial metrics for a portfolio item.
     * Determines current value, invested value, profit, and percentage change.
     *
     * @param entity the portfolio item entity
     * @param currentPrice the current stock price
     * @return summary with calculated metrics for the item
     */
    public PortfolioItemSummary calculate(final PortfolioItemEntity entity, final BigDecimal currentPrice) {
        if (entity == null || currentPrice == null) {
            return emptySummary();
        }

        final double quantity = entity.getQuantity();
        final BigDecimal avgPurchasePrice = entity.getAveragePurchasePrice() != null
                ? entity.getAveragePurchasePrice()
                : BigDecimal.ZERO;

        final BigDecimal investedValue = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
        final BigDecimal totalValue = CalculatorHelper.calculateTotalValue(currentPrice, quantity);
        final BigDecimal profitValue = CalculatorHelper.calculateProfitValue(totalValue, investedValue);
        final String profitPercentage = CalculatorHelper.calculateProfitPercentage(profitValue, investedValue);

        return new PortfolioItemSummary(
                quantity,
                investedValue,
                totalValue,
                profitValue,
                profitPercentage,
                avgPurchasePrice
        );
    }

    private PortfolioItemSummary emptySummary() {
        return new PortfolioItemSummary(
                0.0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "0.00%",
                BigDecimal.ZERO
        );
    }
}