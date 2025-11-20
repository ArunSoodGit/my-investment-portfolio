package com.sood.application.portfolio.history;

import com.example.market.grpc.PortfolioHistoryResponse;
import com.example.market.grpc.PortfolioResponse;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioHistoryEntity;
import com.sood.infrastructure.service.PortfolioHistoryService;
import com.sood.infrastructure.service.PortfolioService;
import jakarta.inject.Singleton;
import java.util.List;

/**
 * Manages portfolio history operations.
 * Handles retrieval and persistence of portfolio snapshots and their history data.
 */
@Singleton
public class PortfolioHistoryManager {

    private final PortfolioHistoryService historyService;
    private final PortfolioHistoryResponseBuilder responseBuilder;
    private final PortfolioService portfolioService;

    public PortfolioHistoryManager(final PortfolioHistoryService historyService,
            final PortfolioHistoryResponseBuilder responseBuilder,
            final PortfolioService portfolioService) {
        this.historyService = historyService;
        this.responseBuilder = responseBuilder;
        this.portfolioService = portfolioService;
    }

    /**
     * Retrieves portfolio history for a given portfolio ID.
     *
     * @param portfolioId the portfolio identifier
     * @return the portfolio history response
     */
    public PortfolioHistoryResponse get(final Long portfolioId) {
        final PortfolioEntity portfolio = portfolioService.getPortfolio(portfolioId);
        final List<PortfolioHistoryEntity> portfolioHistoryEntities = historyService.findByPortfolio(portfolio);
        return responseBuilder.build(portfolioHistoryEntities);
    }

    /**
     * Saves a portfolio snapshot as history.
     * Transforms portfolio response data into history entity format.
     *
     * @param response the portfolio response to save
     */
    public void save(final PortfolioResponse response) {
        final PortfolioEntity portfolio = portfolioService.getPortfolio(response.getPortfolioId());
        historyService.save(response, portfolio);
    }
}