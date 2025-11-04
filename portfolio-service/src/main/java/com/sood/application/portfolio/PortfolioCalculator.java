package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.sood.application.CalculatorHelper;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

@Singleton
public class PortfolioCalculator {

    public PortfolioSummary summarize(final List<PortfolioItem> items) {
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
        final BigDecimal currentPrice = new BigDecimal(item.getCurrentPrice());
        final double quantity = item.getQuantity();
        return CalculatorHelper.calculateTotalValue(currentPrice, quantity);
    }

    private BigDecimal calculateInvestedValue(final List<PortfolioItem> items) {
        return items.stream()
                .map(this::calculateItemInvestedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemInvestedValue(final PortfolioItem item) {
        final BigDecimal averagePurchasePrice = new BigDecimal(item.getAveragePurchasePrice());
        final double quantity = item.getQuantity();
        return CalculatorHelper.calculateInvestedValue(averagePurchasePrice, quantity);
    }
}