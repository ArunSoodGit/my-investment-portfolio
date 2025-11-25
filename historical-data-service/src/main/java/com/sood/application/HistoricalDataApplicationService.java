package com.sood.application;

import com.example.market.grpc.HistoricalDataGetResponse;
import com.example.market.grpc.HistoricalDataIem;
import com.example.market.grpc.HistoricalDataSaveRequest;
import com.sood.application.command.HistoricalDataCommand;
import com.sood.application.result.HistoricalDataResult;
import jakarta.inject.Singleton;

@Singleton
public class HistoricalDataApplicationService {

    private final HistoricalDataPort historicalDataPort;

    public HistoricalDataApplicationService(final HistoricalDataPort historicalDataPort) {
        this.historicalDataPort = historicalDataPort;
    }

    public HistoricalDataGetResponse getHistoricalData(final Long portfolioId) {
        final HistoricalDataCommand command = new HistoricalDataCommand(portfolioId);
        final HistoricalDataResult result = historicalDataPort.getHistoricalData(command);
        return HistoricalDataGetResponse.newBuilder()
                .setPortfolioId(result.portfolioId())
                .addAllItems(result.historicalDataList().stream()
                        .map(historicalData -> HistoricalDataIem.newBuilder()
                                .setDate(historicalData.date())
                                .setCurrentValue(String.valueOf(historicalData.totalCurrentValue()))
                                .setInvestedValue(String.valueOf(historicalData.totalInvested()))
                                .build())
                        .toList())
                .build();

    }

    public void saveHistoricalData(final HistoricalDataSaveRequest request) {
        final HistoricalDataSaveCommand command = HistoricalDataSaveCommand.from(request);
        historicalDataPort.save(command);
    }
}
