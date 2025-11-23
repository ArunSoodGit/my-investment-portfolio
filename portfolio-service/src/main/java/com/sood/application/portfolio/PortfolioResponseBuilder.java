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

    private final PortfolioSummaryBuilder summaryBuilder;

    public PortfolioResponseBuilder(final PortfolioSummaryBuilder summaryBuilder) {
        this.summaryBuilder = summaryBuilder;
    }

    public PortfolioResponse build(final PortfolioEntity portfolio, final List<PortfolioItem> items) {
        final PortfolioSummary summary = summaryBuilder.build(items);
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
