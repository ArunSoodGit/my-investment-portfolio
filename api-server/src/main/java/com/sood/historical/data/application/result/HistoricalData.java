package com.sood.historical.data.application.result;

import com.example.market.grpc.HistoricalDataIem;
import java.math.BigDecimal;

public record HistoricalData(String date, BigDecimal totalInvested, BigDecimal totalCurrentValue) {
    public static HistoricalData from(final HistoricalDataIem historicalDataIem) {
        return new HistoricalData(historicalDataIem.getDate(),
                new BigDecimal(historicalDataIem.getInvestedValue()),
                new BigDecimal(historicalDataIem.getCurrentValue()));
    }
}
