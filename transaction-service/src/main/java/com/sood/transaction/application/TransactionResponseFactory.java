package com.sood.transaction.application;

import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.Transaction;
import com.example.market.grpc.TransactionGetResponse;
import com.sood.transaction.infrastructure.entity.TransactionEntity;
import jakarta.inject.Singleton;
import java.util.List;
import sood.found.TransactionType;

@Singleton
public class TransactionResponseFactory {

    private final ProfitCalculator profitCalculator;

    public TransactionResponseFactory(final ProfitCalculator profitCalculator) {
        this.profitCalculator = profitCalculator;
    }

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
        final String profitPercentage = profitCalculator.calculateProfitPercentage(entity, marketData);
        return Transaction.newBuilder()
                .setId(entity.getId())
                .setSymbol(entity.getSymbol())
                .setQuantity(entity.getQuantity())
                .setPrice(entity.getPrice())
                .setCurrentPrice(Double.parseDouble(marketData.getPrice()))
                .setDate(entity.getDate().toString())
                .setProfitPercentage(profitPercentage)
                .build();
    }
}
