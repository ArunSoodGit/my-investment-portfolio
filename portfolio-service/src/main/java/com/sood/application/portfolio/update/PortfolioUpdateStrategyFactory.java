package com.sood.application.portfolio.update;

import com.sood.infrastructure.entity.PortfolioEntity;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

@Singleton
public class PortfolioUpdateStrategyFactory {

    private final ExistingItemPortfolioUpdateStrategy existingItemPortfolioUpdateStrategy;
    private final NewItemPortfolioUpdateStrategy newItemPortfolioUpdateStrategy;

    public PortfolioUpdateStrategyFactory(ExistingItemPortfolioUpdateStrategy existingItemPortfolioUpdateStrategy,
            NewItemPortfolioUpdateStrategy newItemPortfolioUpdateStrategy) {
        this.existingItemPortfolioUpdateStrategy = existingItemPortfolioUpdateStrategy;
        this.newItemPortfolioUpdateStrategy = newItemPortfolioUpdateStrategy;
    }

    public PortfolioUpdateStrategy getStrategy(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        return portfolio.hasItem(event.symbol()) ? existingItemPortfolioUpdateStrategy : newItemPortfolioUpdateStrategy;
    }
}
