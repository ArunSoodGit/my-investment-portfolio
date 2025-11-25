package com.sood.application.result;

import java.util.List;

public record HistoricalDataResult(Long portfolioId, List<HistoricalData> historicalDataList) {
}
