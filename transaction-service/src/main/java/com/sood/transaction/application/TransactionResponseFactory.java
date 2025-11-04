package com.sood.transaction.application;

import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.Transaction;
import com.example.market.grpc.TransactionGetResponse;
import com.sood.transaction.infrastructure.entity.TransactionEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import sood.found.TransactionType;

@Singleton
public class TransactionResponseFactory {

    public TransactionGetResponse create(final MarketDataResponse marketData, final List<TransactionEntity> entities) {
        final List<Transaction> transactions = entities.stream()
                .filter(entity -> entity.getType() == TransactionType.BUY)
                .map(entity -> buildTransaction(marketData, entity))
                .toList();

        return TransactionGetResponse.newBuilder()
                .setStatus("OK")
                .addAllTransactions(transactions)
                .build();
    }

    private Transaction buildTransaction(final MarketDataResponse marketData, final TransactionEntity entity) {
        return Transaction.newBuilder()
                .setId(entity.getId())
                .setSymbol(entity.getSymbol())
                .setQuantity(entity.getQuantity())
                .setPrice(entity.getPrice())
                .setCurrentPrice(Double.parseDouble(marketData.getPrice()))
                .setDate(entity.getDate().toString())
                .setProfitPercentage(calculateProfitPercentage(entity, marketData))
                .build();
    }

    /**
     * Oblicza procentowy zysk/stratę dla transakcji na podstawie aktualnej ceny akcji.
     */
    private String calculateProfitPercentage(final TransactionEntity entity, final MarketDataResponse marketData) {
        final BigDecimal currentPrice = new BigDecimal(marketData.getPrice());
        final BigDecimal purchasePrice = BigDecimal.valueOf(entity.getPrice());

        if (purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return "0%";
        }

        final BigDecimal profitPercent = currentPrice.subtract(purchasePrice)
                .divide(purchasePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return profitPercent.setScale(2, RoundingMode.HALF_UP) + "%";
    }
}
