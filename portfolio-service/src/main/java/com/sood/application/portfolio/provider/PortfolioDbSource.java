package com.sood.application.portfolio.provider;

import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import jakarta.inject.Singleton;

/**
 * Database source for retrieving portfolio entities.
 * Delegates to the portfolio service for direct database access.
 */
@Singleton
public class PortfolioDbSource implements PortfolioSource {

    private final PortfolioService portfolioService;

    public PortfolioDbSource(final PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Retrieves a portfolio from the database.
     *
     * @param portfolioId the portfolio identifier
     * @return the portfolio entity from database
     */
    @Override
    public PortfolioEntity get(final Long portfolioId) {
        return portfolioService.getPortfolio(portfolioId);
    }
}