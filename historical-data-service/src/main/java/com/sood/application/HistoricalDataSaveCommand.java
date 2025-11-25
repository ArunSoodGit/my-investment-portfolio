package com.sood.application;

import com.example.market.grpc.HistoricalDataIem;
import com.example.market.grpc.HistoricalDataSaveRequest;

public record HistoricalDataSaveCommand(long portfolioId, String date, String investedValue, String currentValue) {

    public static HistoricalDataSaveCommand from(final HistoricalDataSaveRequest request) {
        final HistoricalDataIem historicalDataItem = request.getHistoricalDataItem();
        return new HistoricalDataSaveCommand(request.getPortfolioId(), historicalDataItem.getDate(), historicalDataItem.getInvestedValue(),
                historicalDataItem.getCurrentValue());
    }
}
