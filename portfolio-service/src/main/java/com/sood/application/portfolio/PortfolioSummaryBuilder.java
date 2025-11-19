package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.sood.application.CalculatorHelper;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 * Calculates portfolio-level financial metrics.
 * Aggregates data from individual portfolio items to compute total values and profit/loss.
 */
@Singleton
@Log4j2
public class PortfolioSummaryBuilder {
    public PortfolioSummary build(final List<PortfolioItem> items) {
        final BigDecimal currentValue = calculateCurrentValue(items);
        final BigDecimal investedValue = calculateInvestedValue(items);
        final BigDecimal profitValue = CalculatorHelper.calculateProfitValue(currentValue, investedValue);
        final String profitPercentage = CalculatorHelper.calculateProfitPercentage(profitValue, investedValue);

        return new PortfolioSummary(currentValue, investedValue, profitValue, profitPercentage);
    }

    private BigDecimal calculateCurrentValue(final List<PortfolioItem> items) {
        return items.stream()
                .map(this::calculateItemCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemCurrentValue(final PortfolioItem item) {
        final BigDecimal currentPrice = parsePrice(item.getCurrentPrice());
        final double quantity = item.getQuantity();
        return CalculatorHelper.calculateTotalValue(currentPrice, quantity);
    }

    private BigDecimal calculateInvestedValue(final List<PortfolioItem> items) {
        return items.stream()
                .map(this::calculateItemInvestedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemInvestedValue(final PortfolioItem item) {
        final BigDecimal averagePurchasePrice = parsePrice(item.getAveragePurchasePrice());
        final double quantity = item.getQuantity();
        return CalculatorHelper.calculateTotalValue(averagePurchasePrice, quantity);
    }

    private BigDecimal parsePrice(final String priceStr) {
        if (priceStr == null || priceStr.isBlank()) {
            log.warn("Invalid price value: {}", priceStr);
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(priceStr);
        } catch (NumberFormatException e) {
            log.error("Failed to parse price: {}", priceStr, e);
            return BigDecimal.ZERO;
        }
    }
}