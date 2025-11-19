package com.sood.application.portfolio.update.strategy;

import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

/**
 * Factory for creating appropriate portfolio update strategies.
 * Determines whether to use new item or existing item update strategy based on portfolio state.
 */
@Singleton
public class PortfolioUpdateStrategyFactory {

    private final BuyStrategy buyStrategy;
    private final SellStrategy sellStrategy;

    public PortfolioUpdateStrategyFactory(final BuyStrategy buyStrategy, final SellStrategy sellStrategy) {
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
    }

    public PortfolioUpdateStrategy get(final TransactionCreatedEvent event) {
        return switch (event.type()) {
            case BUY -> buyStrategy;
            case SELL -> sellStrategy;
        };
    }
}
