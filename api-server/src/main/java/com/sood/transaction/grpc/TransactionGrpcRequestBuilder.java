package com.sood.transaction.grpc;

import com.example.market.grpc.TransactionAddRequest;
import com.example.market.grpc.TransactionGetRequest;
import com.example.market.grpc.TransactionRemoveRequest;
import jakarta.inject.Singleton;

@Singleton
public class TransactionGrpcRequestBuilder {

    public TransactionGetRequest buildGetRequest(final Long portfolioId, final String symbol) {
        return TransactionGetRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .setSymbol(symbol)
                .build();
    }

    public TransactionAddRequest buildAddRequest(
            final Long portfolioId,
            final TransactionGrpcRequest transactionGrpcRequest) {
        return TransactionAddRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .setSymbol(transactionGrpcRequest.getSymbol())
                .setQuantity(transactionGrpcRequest.getQuantity())
                .setPrice(transactionGrpcRequest.getPurchasePrice())
                .setDate(transactionGrpcRequest.getDate())
                .build();
    }

    public TransactionRemoveRequest buildRemoveRequest(final Long transactionId) {
        return TransactionRemoveRequest.newBuilder()
                .setTransactionId(transactionId)
                .build();
    }
}
