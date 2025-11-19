package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
import com.sood.application.portfolio.update.strategy.PortfolioUpdateStrategy;
import com.sood.application.portfolio.update.strategy.PortfolioUpdateStrategyFactory;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import sood.found.TransactionCreatedEvent;

/**
 * Handles portfolio updates based on transaction events.
 * Uses strategy pattern to apply appropriate update logic based on transaction type.
 */
@Singleton
@Slf4j
public class PortfolioUpdateHandler {

    private final PortfolioService portfolioService;
    private final PortfolioUpdateStrategyFactory portfolioUpdateStrategyFactory;
    private final PortfolioEventPublisher eventPublisher;
    private final PortfolioPersistenceService persistenceService;

    public PortfolioUpdateHandler(final PortfolioService portfolioService,
            final PortfolioUpdateStrategyFactory updateStrategyFactory, final PortfolioEventPublisher eventPublisher,
            final PortfolioPersistenceService persistenceService) {
        this.portfolioService = portfolioService;
        this.portfolioUpdateStrategyFactory = updateStrategyFactory;
        this.eventPublisher = eventPublisher;
        this.persistenceService = persistenceService;
    }

    @Transactional
    public void handle(final TransactionCreatedEvent event) {
        final PortfolioEntity portfolio = portfolioService.getPortfolio(event.portfolioId());
        final PortfolioUpdateStrategy strategy = portfolioUpdateStrategyFactory.get(event);
        strategy.update(portfolio, event);
        persistenceService.persist(portfolio);
    }

    @Transactional
    public void handle() {
        final List<PortfolioEntity> portfolios = portfolioService.findAll();
        portfolios.forEach(eventPublisher::emit);
    }
}