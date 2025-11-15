package com.sood.application.portfolio;

import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.cache.PortfolioCacheManager;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import com.sood.infrastructure.repository.PortfolioRepository;
import com.sood.infrastructure.service.PortfolioService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

@Singleton
@Slf4j
public class PortfolioUpdateHandler {

    private final PortfolioService portfolioService;
    private final PortfolioItemFactory itemFactory;
    private final PortfolioRepository repository;
    private final PortfolioCacheManager cacheManager;

    public PortfolioUpdateHandler(final PortfolioService portfolioService, final PortfolioItemFactory itemFactory,
            final PortfolioRepository repository, final PortfolioCacheManager cacheManager) {
        this.portfolioService = portfolioService;
        this.itemFactory = itemFactory;
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public void handle(final TransactionCreatedEvent event) {
        final Long portfolioId = event.portfolioId();
        final String symbol = event.symbol();
        final PortfolioEntity portfolio = portfolioService.getPortfolio(portfolioId);
        final Optional<PortfolioItemEntity> existingOpt = findPortfolioItem(event, portfolio);

        if (existingOpt.isPresent()) {
            final PortfolioItemEntity existingItem = existingOpt.get();
            final PortfolioItemEntity updated = itemFactory.updateItem(event, existingItem);

            if (updated.getQuantity() <= 0) {
                portfolio.removeItem(updated);
                log.info("Position closed and removed for symbol {} (portfolioId {})", symbol, portfolioId);
            } else {
                portfolio.addItem(updated);
                log.info("Position updated for symbol {} (portfolioId {}) - new qty: {}", symbol, portfolioId, updated.getQuantity());
            }
        } else {
            if (isBuyTransaction(event)) {
                final PortfolioItemEntity created = itemFactory.createItem(event);
                portfolio.addItem(created);
                log.info("Created new position for symbol {} (portfolioId {}) - qty: {}", symbol, portfolioId, created.getQuantity());
            } else {
                log.warn("Received SELL for non-existing position: symbol {} (portfolioId {}). Ignoring transaction.", symbol, portfolioId);
            }
        }

        repository.update(portfolio);
        cacheManager.put(portfolio);
        PortfolioEventPublisher.emit(portfolio);
    }

    private Optional<PortfolioItemEntity> findPortfolioItem(final TransactionCreatedEvent event, final PortfolioEntity portfolio) {
        return portfolio.getItems().stream()
                .filter(item -> item.getSymbol().equals(event.symbol()))
                .findFirst();
    }

    private boolean isBuyTransaction(final TransactionCreatedEvent event) {
        return TransactionType.BUY == event.type();
    }
}