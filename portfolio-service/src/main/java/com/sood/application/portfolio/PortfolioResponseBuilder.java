package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.example.market.grpc.PortfolioResponse;
import com.sood.infrastructure.entity.PortfolioEntity;
import jakarta.inject.Singleton;
import java.util.List;

/**
 * Builds gRPC portfolio response objects from portfolio entities and items.
 * Calculates and aggregates summary metrics for the response.
 */
@Singleton
public class PortfolioResponseBuilder {

    private final PortfolioCalculator calculator;

    public PortfolioResponseBuilder(final PortfolioCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Builds a gRPC PortfolioResponse from a portfolio entity and its items.
     * Includes calculated summary metrics like total value and profit.
     *
     * @param portfolio the portfolio entity
     * @param items the portfolio items with current market data
     * @return the constructed portfolio response
     */
    public PortfolioResponse build(final PortfolioEntity portfolio, final List<PortfolioItem> items) {
        final PortfolioSummary summary = calculator.summarize(items);
        return PortfolioResponse.newBuilder()
                .setPortfolioId(portfolio.getId())
                .setUserId(portfolio.getUserId())
                .addAllItems(items)
                .setPortfolioName(portfolio.getPortfolioName())
                .setCurrentValue(summary.currentValue().toPlainString())
                .setInvestedValue(summary.investedValue().toPlainString())
                .setProfitValue(summary.profitValue().toPlainString())
                .setProfitPercentage(summary.profitPercentage())
                .build();
    }
}
