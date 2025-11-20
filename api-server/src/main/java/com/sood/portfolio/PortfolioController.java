package com.sood.portfolio;

import com.sood.portfolio.history.PortfolioHistoryDTO;
import com.sood.portfolio.history.PortfolioHistoryService;
import com.sood.portfolio.model.PortfolioResponseDTO;
import com.sood.portfolio.stream.PortfolioStreamService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import java.util.List;

@Controller("/v1/api/portfolio")
public class PortfolioController {

    private final PortfolioStreamService portfolioStreamService;
    private final PortfolioHistoryService historyService;

    @Inject
    public PortfolioController(final PortfolioStreamService portfolioStreamService, final PortfolioHistoryService historyService) {
        this.portfolioStreamService = portfolioStreamService;
        this.historyService = historyService;
    }

    @Get("/{portfolioId}")
    @Produces(MediaType.TEXT_EVENT_STREAM)
    public Observable<PortfolioResponseDTO> streamPortfolio(@PathVariable final Long portfolioId) {
        return portfolioStreamService.stream(portfolioId);
    }

    @Get("/{portfolioId}/history")
    public List<PortfolioHistoryDTO> getHistory(@PathVariable final Long portfolioId) {
        return historyService.getPortfolioHistory(portfolioId);
    }
}