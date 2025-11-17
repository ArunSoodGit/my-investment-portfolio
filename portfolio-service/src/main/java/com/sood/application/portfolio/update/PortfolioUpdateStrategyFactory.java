package com.sood.application.portfolio.update;

import com.sood.infrastructure.entity.PortfolioEntity;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

/**
 * Factory for creating appropriate portfolio update strategies.
 * Determines whether to use new item or existing item update strategy based on portfolio state.
 */
@Singleton
public class PortfolioUpdateStrategyFactory {

    private final ExistingItemPortfolioUpdateStrategy existingItemPortfolioUpdateStrategy;
    private final NewItemPortfolioUpdateStrategy newItemPortfolioUpdateStrategy;

    public PortfolioUpdateStrategyFactory(ExistingItemPortfolioUpdateStrategy existingItemPortfolioUpdateStrategy,
            NewItemPortfolioUpdateStrategy newItemPortfolioUpdateStrategy) {
        this.existingItemPortfolioUpdateStrategy = existingItemPortfolioUpdateStrategy;
        this.newItemPortfolioUpdateStrategy = newItemPortfolioUpdateStrategy;
    }

    /**
     * Selects the appropriate update strategy based on whether the portfolio already contains the symbol.
     *
     * @param portfolio the portfolio entity
     * @param event the transaction event
     * @return the appropriate update strategy
     */
    public PortfolioUpdateStrategy getStrategy(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        return portfolio.hasItem(event.symbol()) ? existingItemPortfolioUpdateStrategy : newItemPortfolioUpdateStrategy;
    }
}
