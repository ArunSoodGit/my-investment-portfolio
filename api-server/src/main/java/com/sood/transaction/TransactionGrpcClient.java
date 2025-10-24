package com.sood.transaction;

import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;
import market.Transaction;
import market.TransactionServiceGrpc;

@Singleton
public class TransactionGrpcClient {

    private final TransactionServiceGrpc.TransactionServiceBlockingStub stub;

    public TransactionGrpcClient(@GrpcChannel("transaction") io.grpc.Channel channel) {
        this.stub = TransactionServiceGrpc.newBlockingStub(channel);
    }

    public Transaction.TransactionResponse addTransaction(final String userId, final TransactionDTO transactionDTO) {
        final Transaction.TransactionRequest request = Transaction.TransactionRequest.newBuilder()
                .setUserId(userId)
                .setSymbol(transactionDTO.getSymbol())
                .setQuantity(transactionDTO.getQuantity())
                .setPrice(transactionDTO.getPrice())
                .setTypeValue(transactionDTO.getType().equals("BUY") ? 0 : 1)
                .setDate(transactionDTO.getDate())
                .build();
        return stub.addTransaction(request);
    }
}
