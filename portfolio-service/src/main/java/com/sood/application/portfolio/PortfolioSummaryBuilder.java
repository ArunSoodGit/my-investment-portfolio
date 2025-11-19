package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.sood.application.CalculatorHelper;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.log4j.Log4j2;

import static com.sood.application.portfolio.util.PriceUtils.parsePrice;

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
}