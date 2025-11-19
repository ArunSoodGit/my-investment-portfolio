package com.sood.application.portfolio.provider;

import com.sood.infrastructure.entity.PortfolioEntity;
import jakarta.inject.Singleton;
import java.util.Optional;

/**
 * Provides portfolio data with a two-level caching strategy.
 * First attempts to retrieve from cache, then falls back to database.
 */
@Singleton
public class PortfolioProvider {

    private final PortfolioCacheSource cacheManager;
    private final PortfolioDbSource dbSource;

    public PortfolioProvider(final PortfolioCacheSource cacheManager, final PortfolioDbSource dbSource) {
        this.cacheManager = cacheManager;
        this.dbSource = dbSource;
    }

    /**
     * Provides a portfolio entity, retrieving from cache if available, otherwise from database.
     * Cache is populated on database retrieval for future requests.
     *
     * @param portfolioId the portfolio identifier
     * @return the portfolio entity
     */
    public PortfolioEntity provide(final Long portfolioId) {
        return Optional.ofNullable(cacheManager.get(portfolioId))
                .orElseGet(() -> fromDb(portfolioId));
    }

    private PortfolioEntity fromDb(final Long portfolioId) {
        final PortfolioEntity entity = dbSource.get(portfolioId);
        cacheManager.put(entity);
        return entity;
    }
}