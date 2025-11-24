package com.sood.transaction.application;

import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.TransactionGetResponse;
import com.sood.transaction.domain.model.Transaction;
import com.sood.transaction.application.service.ProfitCalculator;
import jakarta.inject.Singleton;
import java.util.List;
import sood.found.TransactionType;

@Singleton
public class TransactionResponseFactory {

    private final ProfitCalculator profitCalculator;

    public TransactionResponseFactory(final ProfitCalculator profitCalculator) {
        this.profitCalculator = profitCalculator;
    }

    public TransactionGetResponse create(final MarketDataResponse marketData,
            final List<Transaction> transactions) {
        final List<com.example.market.grpc.Transaction> mappedTransactions = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.BUY)
                .map(transaction -> buildTransaction(marketData, transaction))
                .toList();

        return TransactionGetResponse.newBuilder()
                .setStatus("OK")
                .addAllTransactions(mappedTransactions)
                .build();
    }

    private com.example.market.grpc.Transaction buildTransaction(final MarketDataResponse marketData,
            final Transaction transaction) {
        final String profitPercentage = profitCalculator.calculateProfitPercentage(transaction, marketData);

        return com.example.market.grpc.Transaction.newBuilder()
                .setId(transaction.getId())
                .setSymbol(transaction.getSymbol())
                .setQuantity(transaction.getQuantity())
                .setPrice(transaction.getPrice())
                .setCurrentPrice(Double.parseDouble(marketData.getPrice()))
                .setDate(transaction.getDate().toString())
                .setProfitPercentage(profitPercentage)
                .build();
    }
}
