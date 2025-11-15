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
                        .map(PortfolioHistoryResponseBuilder::toHistoryItem)
                        .toList())
                .build();
    }

    private static PortfolioHistoryItem toHistoryItem(final PortfolioHistoryEntity portfolioHistoryEntity) {
        return PortfolioHistoryItem.newBuilder()
                .setDate(String.valueOf(portfolioHistoryEntity.getDate()))
                .setInvestedValue(String.valueOf(portfolioHistoryEntity.getInvestedValue()))
                .setCurrentValue(String.valueOf(portfolioHistoryEntity.getCurrentValue()))
                .build();
    }
}
