package com.sood.application.portfolio;

import java.math.BigDecimal;

public record PortfolioSummary(
        BigDecimal currentValue,
        BigDecimal investedValue,
        BigDecimal profitValue,
        String profitPercentage
) {
}