package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.cache.PortfolioCacheManager;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import com.sood.infrastructure.repository.PortfolioRepository;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

@Singleton
@Log4j2
public class NewItemPortfolioUpdateStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemFactory itemFactory;
    private final PortfolioRepository repository;
    private final PortfolioCacheManager cacheManager;

    public NewItemPortfolioUpdateStrategy(final PortfolioItemFactory itemFactory, final PortfolioRepository repository,
            final PortfolioCacheManager cacheManager) {
        this.itemFactory = itemFactory;
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

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
        PortfolioEventPublisher.emit(portfolio);
    }

    private boolean isBuyTransaction(final TransactionCreatedEvent event) {
        return TransactionType.BUY == event.type();
    }
}
