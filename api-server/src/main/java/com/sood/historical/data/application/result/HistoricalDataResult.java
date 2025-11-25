package com.sood.historical.data.application.result;

import com.example.market.grpc.HistoricalDataGetResponse;
import java.util.List;

public record HistoricalDataResult(Long portfolioId, List<HistoricalData> historicalDataList) {
    public static HistoricalDataResult from(final HistoricalDataGetResponse response) {
        final List<HistoricalData> historicalData = response.getItemsList().stream()
                .map(HistoricalData::from)
                .toList();
        return new HistoricalDataResult(response.getPortfolioId(), historicalData);
    }
}
