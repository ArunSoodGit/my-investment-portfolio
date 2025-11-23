package com.sood.application.portfolio.provider;

import com.sood.infrastructure.entity.PortfolioEntity;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class PortfolioProvider {

    private final PortfolioCacheSource cacheManager;
    private final PortfolioDbSource dbSource;

    public PortfolioProvider(final PortfolioCacheSource cacheManager, final PortfolioDbSource dbSource) {
        this.cacheManager = cacheManager;
        this.dbSource = dbSource;
    }

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