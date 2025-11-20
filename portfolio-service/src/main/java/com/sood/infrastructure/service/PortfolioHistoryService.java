package com.sood.infrastructure.service;

import com.example.market.grpc.PortfolioResponse;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioHistoryEntity;
import com.sood.infrastructure.repository.PortfolioHistoryRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Singleton
public class PortfolioHistoryService {

    private final PortfolioHistoryRepository repository;

    public PortfolioHistoryService(final PortfolioHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<PortfolioHistoryEntity> findByPortfolio(final PortfolioEntity portfolio) {
        return repository.findByPortfolio(portfolio);
    }

    @Transactional
    public void save(final PortfolioResponse response, final PortfolioEntity portfolio) {
        final PortfolioHistoryEntity historyEntity = new PortfolioHistoryEntity();
        historyEntity.setDate(LocalDateTime.now().withSecond(0).withNano(0));
        historyEntity.setPortfolio(portfolio);
        historyEntity.setCurrentValue(new BigDecimal(response.getCurrentValue()));
        historyEntity.setInvestedValue(new BigDecimal(response.getInvestedValue()));

        repository.save(historyEntity);
    }
}
