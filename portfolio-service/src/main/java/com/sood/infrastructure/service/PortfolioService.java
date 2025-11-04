package com.sood.infrastructure.service;

import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.repository.PortfolioRepository;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.NoSuchElementException;

@Singleton
public class PortfolioService {

    private static final String PORTFOLIO_NOT_FOUND_MESSAGE = "Portfolio nie istnieje dla id: %s";

    private final PortfolioRepository repository;

    public PortfolioService(final PortfolioRepository repository) {
        this.repository = repository;
    }

    public PortfolioEntity getPortfolio(final Long portfolioId) {
        return repository.findByIdWithItemsAndTransactions(portfolioId)
                .orElseThrow(() -> new NoSuchElementException(String.format(PORTFOLIO_NOT_FOUND_MESSAGE, portfolioId)));
    }

    public List<PortfolioEntity> findAll() {
        return repository.findAllWithItemsAndTransactions();
    }
}