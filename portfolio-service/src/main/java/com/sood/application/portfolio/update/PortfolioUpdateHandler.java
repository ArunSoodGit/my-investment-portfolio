package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
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

    public PortfolioUpdateHandler(final PortfolioService portfolioService,
            final PortfolioUpdateStrategyFactory updateStrategyFactory, final PortfolioEventPublisher eventPublisher) {
        this.portfolioService = portfolioService;
        this.portfolioUpdateStrategyFactory = updateStrategyFactory;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Processes a transaction event and updates the portfolio accordingly.
     * Executes within a database transaction to ensure consistency.
     *
     * @param event the transaction created event to process
     */
    @Transactional
    public void handle(final TransactionCreatedEvent event) {
        final Long portfolioId = event.portfolioId();
        final PortfolioEntity portfolio = portfolioService.getPortfolio(portfolioId);
        final PortfolioUpdateStrategy strategy = portfolioUpdateStrategyFactory.getStrategy(portfolio, event);
        strategy.update(portfolio, event);
    }

    @Transactional
    public void handle() {
        final List<PortfolioEntity> portfolios = portfolioService.findAll();
        portfolios.forEach(eventPublisher::emit);
    }
}