package com.sood.application.result;

import java.math.BigDecimal;

public record HistoricalData(String date, BigDecimal totalInvested, BigDecimal totalCurrentValue) {
}
