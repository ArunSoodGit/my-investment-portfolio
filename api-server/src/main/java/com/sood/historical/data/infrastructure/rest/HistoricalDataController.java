package com.sood.historical.data.infrastructure.rest;

import com.sood.historical.data.infrastructure.rest.dto.HistoricalDataDTO;
import com.sood.historical.data.application.HistoricalDataApplicationService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

@Controller("/v1/api/historical-data")

public class HistoricalDataController {

    private final HistoricalDataApplicationService service;

    public HistoricalDataController(final HistoricalDataApplicationService service) {
        this.service = service;
    }

    @Get("/{portfolioId}")
    public HttpResponse<HistoricalDataDTO> getHistory(@PathVariable final Long portfolioId) {
        return service.getHistoricalData(portfolioId);
    }
}
