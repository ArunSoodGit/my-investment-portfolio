package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.application.portfolio.provider.PortfolioCacheSource;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.repository.PortfolioRepository;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import sood.found.TransactionCreatedEvent;

/**
 * Updates an existing portfolio item based on a transaction.
 * Handles buy and sell operations for positions already in the portfolio.
 * Removes positions when quantity reaches zero and emits update events.
 */
@Singleton
@Slf4j
public class ExistingItemPortfolioUpdateStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemFactory itemFactory;
    private final PortfolioRepository repository;
    private final PortfolioCacheSource cacheManager;
    private final PortfolioEventPublisher eventPublisher;

    public ExistingItemPortfolioUpdateStrategy(final PortfolioItemFactory itemFactory, final PortfolioRepository repository,
            final PortfolioCacheSource cacheManager, final PortfolioEventPublisher eventPublisher) {
        this.itemFactory = itemFactory;
        this.repository = repository;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Updates an existing portfolio item with a new transaction.
     * Persists changes to database, cache, and emits event for subscribers.
     *
     * @param portfolio the portfolio entity
     * @param event     the transaction event
     */
    @Override
    public void update(PortfolioEntity portfolio, TransactionCreatedEvent event) {
        var item = portfolio.findItemBySymbol(event.symbol());
        var updated = itemFactory.updateItem(event, item);

        if (updated.getQuantity() <= 0) {
            portfolio.removeItem(updated);
        } else {
            portfolio.addItem(updated);
        }
    }
}
