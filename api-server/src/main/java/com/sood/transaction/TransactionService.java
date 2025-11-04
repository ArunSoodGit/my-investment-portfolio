package com.sood.transaction;

import com.sood.transaction.model.TransactionDTO;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class TransactionService {

    private final TransactionGrpcClient grpcClient;

    @Inject
    public TransactionService(final TransactionGrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }

    public Single<List<TransactionDTO>> getTransactions(final Long portfolioId, final String symbol) {
        return grpcClient.getTransactions(portfolioId, symbol)
                .map(response -> response.getTransactionsList().stream()
                        .map(TransactionDTO::fromProto)
                        .toList());
    }

    public Single<Boolean> addTransaction(final Long portfolioId, final TransactionRequest request) {
        return grpcClient.addTransaction(portfolioId, request)
                .map(response -> response.getStatus().equals("OK"));
    }

    public Single<Boolean> removeTransaction(final Long transactionId) {
        return grpcClient.removeTransaction(transactionId)
                .map(response -> response.getStatus().equals("OK"));
    }
}