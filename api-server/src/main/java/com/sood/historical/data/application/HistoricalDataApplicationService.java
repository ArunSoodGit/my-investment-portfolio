package com.sood.historical.data.application;

import com.sood.historical.data.infrastructure.rest.dto.HistoricalDataDTO;
import com.sood.historical.data.application.command.HistoricalDataCommand;
import com.sood.historical.data.application.port.HistoricalDataPort;
import com.sood.historical.data.application.result.HistoricalDataResult;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;

@Singleton
public class HistoricalDataApplicationService {

    private final HistoricalDataPort historicalDataPort;

    public HistoricalDataApplicationService(final HistoricalDataPort historicalDataPort) {
        this.historicalDataPort = historicalDataPort;
    }

    public HttpResponse<HistoricalDataDTO> getHistoricalData(final Long portfolioId) {
        final HistoricalDataCommand command = new HistoricalDataCommand(portfolioId);
        final HistoricalDataResult result = historicalDataPort.getHistoricalData(command);
        final HistoricalDataDTO dto = HistoricalDataDTO.from(result);
        return HttpResponse.ok(dto);
    }
}
