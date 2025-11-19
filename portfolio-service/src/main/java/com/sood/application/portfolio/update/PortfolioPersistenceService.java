package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
import com.sood.application.portfolio.provider.PortfolioCacheSource;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.repository.PortfolioRepository;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioPersistenceService {

    private final PortfolioRepository repository;
    private final PortfolioCacheSource cache;
    private final PortfolioEventPublisher publisher;

    public PortfolioPersistenceService(PortfolioRepository repository, PortfolioCacheSource cache,
            PortfolioEventPublisher publisher) {
        this.repository = repository;
        this.cache = cache;
        this.publisher = publisher;
    }

    public void persist(final PortfolioEntity portfolio) {
        repository.update(portfolio);
        cache.put(portfolio);
        publisher.emit(portfolio);
    }

}
