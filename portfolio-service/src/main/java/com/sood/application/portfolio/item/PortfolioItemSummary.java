package com.sood.application.portfolio.item;

import java.math.BigDecimal;

public record PortfolioItemSummary(
        double totalQuantity,
        BigDecimal investedValue,
        BigDecimal totalValue,
        BigDecimal profitValue,
        String profitPercentage,
        BigDecimal averagePurchasePrice
) {}