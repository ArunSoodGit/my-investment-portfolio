package com.sood.transaction;

import com.example.market.grpc.TransactionGetResponse;
import com.example.market.grpc.TransactionResponse;
import com.sood.transaction.grpc.TransactionGrpcRequest;
import io.reactivex.rxjava3.core.Single;

public interface TransactionClient {
    Single<TransactionGetResponse> getTransactions(Long portfolioId, String symbol);
    Single<TransactionResponse> addTransaction(Long portfolioId, TransactionGrpcRequest request);
    Single<TransactionResponse> removeTransaction(Long transactionId);
}
