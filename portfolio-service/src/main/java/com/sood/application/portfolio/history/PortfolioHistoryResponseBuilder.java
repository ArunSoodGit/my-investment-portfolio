package com.sood.application.portfolio.history;

import com.example.market.grpc.PortfolioHistoryItem;
import com.example.market.grpc.PortfolioHistoryResponse;
import com.sood.infrastructure.entity.PortfolioHistoryEntity;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class PortfolioHistoryResponseBuilder {

    public PortfolioHistoryResponse build(final List<PortfolioHistoryEntity> portfolioHistoryEntities) {
        return PortfolioHistoryResponse.newBuilder()
                .addAllItems(portfolioHistoryEntities.stream()
                        .map(portfolioHistoryEntity -> PortfolioHistoryItem.newBuilder()
                                .setDate(String.valueOf(portfolioHistoryEntity.getDate()))
                                .setInvestedValue(String.valueOf(portfolioHistoryEntity.getInvestedValue()))
                                .setCurrentValue(String.valueOf(portfolioHistoryEntity.getCurrentValue()))
                                .build())
                        .toList())
                .build();
    }
}
