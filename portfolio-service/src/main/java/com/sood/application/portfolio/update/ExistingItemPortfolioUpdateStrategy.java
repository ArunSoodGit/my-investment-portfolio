package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.application.portfolio.provider.PortfolioCacheSource;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
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

    public ExistingItemPortfolioUpdateStrategy(final PortfolioItemFactory itemFactory, final PortfolioRepository repository,
            final PortfolioCacheSource cacheManager) {
        this.itemFactory = itemFactory;
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    /**
     * Updates an existing portfolio item with a new transaction.
     * Persists changes to database, cache, and emits event for subscribers.
     *
     * @param portfolio the portfolio entity
     * @param event the transaction event
     */
    @Override
    public void update(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        final String symbol = event.symbol();
        final Long portfolioId = portfolio.getId();
        final PortfolioItemEntity existing = findPortfolioItem(event, portfolio);
        final PortfolioItemEntity updated = itemFactory.updateItem(event, existing);

        if (updated.getQuantity() <= 0) {
            portfolio.removeItem(updated);
            log.info("Position closed and removed for symbol {} (portfolioId {})", symbol, portfolioId);
        } else {
            portfolio.addItem(updated);
            log.info("Position updated for symbol {} (portfolioId {}) - new qty: {}", symbol, portfolioId, updated.getQuantity());
        }
        repository.update(portfolio);
        cacheManager.put(portfolio);
        PortfolioEventPublisher.emit(portfolio);
    }

    private PortfolioItemEntity findPortfolioItem(final TransactionCreatedEvent event, final PortfolioEntity portfolio) {
        return portfolio.getItems().stream()
                .filter(item -> item.getSymbol().equals(event.symbol()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Portfolio item not found for symbol: " + event.symbol()));
    }
}
