package com.sood.portfolio.history;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class PortfolioHistoryService {

    private final PortfolioHistoryGrpcClient historyClient;

    @Inject
    public PortfolioHistoryService(final PortfolioHistoryGrpcClient historyClient) {
        this.historyClient = historyClient;
    }

    public List<PortfolioHistoryDTO> getPortfolioHistory(final Long portfolioId) {
        return historyClient.getPortfolioHistory(portfolioId)
                .getItemsList()
                .stream()
                .map(PortfolioHistoryDTO::fromProto)
                .toList();
    }
}