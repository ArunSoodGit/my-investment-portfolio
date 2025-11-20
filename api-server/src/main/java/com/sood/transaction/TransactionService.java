package com.sood.transaction;

import com.sood.transaction.grpc.TransactionGrpcRequest;
import com.sood.transaction.model.TransactionDTO;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class TransactionService {

    private final TransactionClient transactionClient;

    @Inject
    public TransactionService(final TransactionClient transactionClient) {
        this.transactionClient = transactionClient;
    }

    public Single<List<TransactionDTO>> getTransactions(final Long portfolioId, final String symbol) {
        return transactionClient.getTransactions(portfolioId, symbol)
                .map(response -> response.getTransactionsList().stream()
                        .map(TransactionDTO::fromProto)
                        .toList())
                .onErrorReturn(throwable -> {
                    throw new RuntimeException("Failed to fetch transactions", throwable);
                });
    }

    public Single<Boolean> addTransaction(final Long portfolioId, final TransactionGrpcRequest request) {
        return transactionClient.addTransaction(portfolioId, request)
                .map(response -> response.getStatus().equals("OK"))
                .onErrorReturn(throwable -> {
                    throw new RuntimeException("Failed to add transaction", throwable);
                });
    }

    public Single<Boolean> removeTransaction(final Long transactionId) {
        return transactionClient.removeTransaction(transactionId)
                .map(response -> response.getStatus().equals("OK"))
                .onErrorReturn(throwable -> {
                    throw new RuntimeException("Failed to remove transaction", throwable);
                });
    }
}