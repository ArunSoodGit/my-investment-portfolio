package com.sood.transaction.grpc;

import com.sood.transaction.TransactionService;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Inject;
import market.Transaction;
import market.TransactionServiceGrpc;

@GrpcService
public class TransactionGrpcEndpoint extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final TransactionService service;

    @Inject
    public TransactionGrpcEndpoint(final TransactionService service) {
        this.service = service;
    }

    @Override
    public void addTransaction(final Transaction.TransactionRequest request,
            final StreamObserver<Transaction.TransactionResponse> responseObserver) {
        final Transaction.TransactionResponse response = service.addTransaction(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}