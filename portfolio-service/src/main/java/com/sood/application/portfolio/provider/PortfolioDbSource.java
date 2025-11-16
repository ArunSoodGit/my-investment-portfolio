package com.sood.application.portfolio.provider;

import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioDbSource implements PortfolioSource {

    private final PortfolioService portfolioService;

    public PortfolioDbSource(final PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Override
    public PortfolioEntity get(final Long portfolioId) {
        return portfolioService.getPortfolio(portfolioId);
    }
}