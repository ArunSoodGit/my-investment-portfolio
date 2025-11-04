package com.sood.application.portfolio;

import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import sood.found.TransactionCreatedEvent;

@Singleton
@Slf4j
public class PortfolioUpdateHandler {

    private final PortfolioService portfolioService;
    private final PortfolioUpdateStrategyFactory portfolioUpdateStrategyFactory;

    public PortfolioUpdateHandler(final PortfolioService portfolioService, PortfolioUpdateStrategyFactory updateStrategyFactory) {
        this.portfolioService = portfolioService;
        this.portfolioUpdateStrategyFactory = updateStrategyFactory;
    }

    @Transactional
    public void handle(final TransactionCreatedEvent event) {
        final Long portfolioId = event.portfolioId();
        final PortfolioEntity portfolio = portfolioService.getPortfolio(portfolioId);
        final PortfolioUpdateStrategy strategy = portfolioUpdateStrategyFactory.getStrategy(portfolio, event);
        strategy.update(portfolio, event);
    }
}