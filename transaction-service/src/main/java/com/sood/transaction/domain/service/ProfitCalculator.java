package com.sood.transaction.domain.service;

import com.example.market.grpc.MarketDataResponse;
import com.sood.transaction.domain.model.Transaction;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Singleton
public class ProfitCalculator {

    /**
     * Oblicza procentowy zysk/stratę dla transakcji na podstawie aktualnej ceny akcji.
     */
    public String calculateProfitPercentage(final Transaction transaction, final MarketDataResponse marketData) {
        final BigDecimal currentPrice = new BigDecimal(marketData.getPrice());
        final BigDecimal purchasePrice = BigDecimal.valueOf(transaction.getPrice());

        if (purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return "0%";
        }

        final BigDecimal profitPercent = currentPrice.subtract(purchasePrice)
                .divide(purchasePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return profitPercent.setScale(2, RoundingMode.HALF_UP) + "%";
    }
}
