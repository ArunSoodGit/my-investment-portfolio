package com.sood.portfolio;

import com.sood.portfolio.history.PortfolioHistoryDTO;
import com.sood.portfolio.history.PortfolioHistoryService;
import com.sood.portfolio.model.PortfolioResponseDTO;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Inject;
import java.util.List;

@Controller("/v1/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PortfolioHistoryService historyService;

    @Inject
    public PortfolioController(final PortfolioService portfolioService, final PortfolioHistoryService historyService) {
        this.portfolioService = portfolioService;
        this.historyService = historyService;
    }

    @Get("/{portfolioId}")
    @Produces(MediaType.TEXT_EVENT_STREAM)
    public Flowable<PortfolioResponseDTO> streamPortfolio(@PathVariable final Long portfolioId) {
        return portfolioService.streamPortfolio(portfolioId);
    }

    @Get("/{portfolioId}/history")
    public List<PortfolioHistoryDTO> getHistory(@PathVariable final Long portfolioId) {
        return historyService.getPortfolioHistory(portfolioId);
    }
}