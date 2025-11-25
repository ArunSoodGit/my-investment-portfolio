package com.sood.portfolio;

import com.sood.portfolio.model.PortfolioResponseDTO;
import com.sood.portfolio.stream.PortfolioStreamService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;

@Controller("/v1/api/portfolio")
public class PortfolioController {

    private final PortfolioStreamService portfolioStreamService;

    @Inject
    public PortfolioController(final PortfolioStreamService portfolioStreamService) {
        this.portfolioStreamService = portfolioStreamService;
    }

    @Get("/{portfolioId}")
    @Produces(MediaType.TEXT_EVENT_STREAM)
    public Observable<PortfolioResponseDTO> streamPortfolio(@PathVariable final Long portfolioId) {
        return portfolioStreamService.stream(portfolioId);
    }
}