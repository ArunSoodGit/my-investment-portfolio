package com.sood.application.portfolio.update.strategy;

import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

/**
 * Factory for creating appropriate portfolio update strategies.
 * Determines whether to use new item or existing item update strategy based on portfolio state.
 */
@Singleton
public class PortfolioUpdateStrategyFactory {

    private final BuyUpdateStrategy buyUpdateStrategy;
    private final SellUpdateStrategy sellUpdateStrategy;

    public PortfolioUpdateStrategyFactory(final BuyUpdateStrategy buyUpdateStrategy, final SellUpdateStrategy sellUpdateStrategy) {
        this.buyUpdateStrategy = buyUpdateStrategy;
        this.sellUpdateStrategy = sellUpdateStrategy;
    }

    public PortfolioUpdateStrategy get(final TransactionCreatedEvent event) {
        return switch (event.type()) {
            case BUY -> buyUpdateStrategy;
            case SELL -> sellUpdateStrategy;
        };
    }
}
