package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.application.portfolio.provider.PortfolioCacheSource;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import com.sood.infrastructure.repository.PortfolioRepository;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

/**
 * Adds new portfolio items to a portfolio based on buy transactions.
 * Ignores sell transactions for non-existing positions and emits update events.
 */
@Singleton
@Log4j2
public class NewItemPortfolioUpdateStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemFactory itemFactory;
    private final PortfolioRepository repository;
    private final PortfolioCacheSource cacheManager;
    private final PortfolioEventPublisher eventPublisher;

    public NewItemPortfolioUpdateStrategy(final PortfolioItemFactory itemFactory, final PortfolioRepository repository,
            final PortfolioCacheSource cacheManager, final PortfolioEventPublisher eventPublisher) {
        this.itemFactory = itemFactory;
        this.repository = repository;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates and adds a new portfolio item for buy transactions.
     * Ignores sell transactions for non-existing positions.
     * Persists changes and emits update events.
     *
     * @param portfolio the portfolio entity
     * @param event the transaction event
     */
    @Override
    public void update(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        final String symbol = event.symbol();
        final Long portfolioId = portfolio.getId();

        if (isBuyTransaction(event)) {
            final PortfolioItemEntity created = itemFactory.createItem(event);
            portfolio.addItem(created);
            log.info("Created new position for symbol {} (portfolioId {}) - qty: {}", symbol, portfolioId, created.getQuantity());
        } else {
            log.warn("Received SELL for non-existing position: symbol {} (portfolioId {}). Ignoring transaction.", symbol, portfolioId);
        }
        repository.update(portfolio);
        cacheManager.put(portfolio);
        eventPublisher.emit(portfolio);
    }

    private boolean isBuyTransaction(final TransactionCreatedEvent event) {
        return TransactionType.BUY == event.type();
    }
}
