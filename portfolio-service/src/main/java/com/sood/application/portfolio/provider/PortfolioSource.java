package com.sood.application.portfolio.provider;

import com.sood.infrastructure.entity.PortfolioEntity;

/**
 * Interface for portfolio data sources.
 * Defines contract for retrieving portfolio entities from various sources (cache, database, etc).
 */
public interface PortfolioSource {
    /**
     * Retrieves a portfolio by its identifier.
     *
     * @param portfolioId the portfolio identifier
     * @return the portfolio entity
     */
    PortfolioEntity get(Long portfolioId);
}