package com.sood.application.portfolio.provider;

import com.sood.infrastructure.entity.PortfolioEntity;

public interface PortfolioSource {
    PortfolioEntity get(Long portfolioId);
}