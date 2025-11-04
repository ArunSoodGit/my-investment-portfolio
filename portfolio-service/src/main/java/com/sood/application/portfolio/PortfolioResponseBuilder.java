package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.example.market.grpc.PortfolioResponse;
import com.sood.infrastructure.entity.PortfolioEntity;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class PortfolioResponseBuilder {

    private final PortfolioCalculator calculator;

    public PortfolioResponseBuilder(final PortfolioCalculator calculator) {
        this.calculator = calculator;
    }

    public PortfolioResponse build(final PortfolioEntity portfolio, final List<PortfolioItem> items) {
        final PortfolioSummary summary = calculator.summarize(items);
        return PortfolioResponse.newBuilder()
                .setPortfolioId(portfolio.getId())
                .setUserId(portfolio.getUserId())
                .addAllItems(items)
                .setPortfolioName(portfolio.getPortfolioName())
                .setCurrentValue(String.valueOf(summary.currentValue()))
                .setInvestedValue(String.valueOf(summary.investedValue()))
                .setProfitValue(String.valueOf(summary.profitValue()))
                .setProfitPercentage(String.valueOf(summary.profitPercentage()))
                .build();
    }
}
