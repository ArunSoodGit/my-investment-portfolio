package com.sood.transaction.api;

import com.example.market.grpc.TransactionAddRequest;
import com.example.market.grpc.TransactionGetRequest;
import com.example.market.grpc.TransactionGetResponse;
import com.example.market.grpc.TransactionRemoveRequest;
import com.example.market.grpc.TransactionResponse;
import com.example.market.grpc.TransactionServiceGrpc;
import com.sood.transaction.application.TransactionManager;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Inject;

@GrpcService
public class TransactionController extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final TransactionManager service;

    @Inject
    public TransactionController(final TransactionManager service) {
        this.service = service;
    }

    @Override
    public void addTransaction(final TransactionAddRequest request, final StreamObserver<TransactionResponse> responseObserver) {
        service.addTransaction(request)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> responseObserver.onError(Status.INTERNAL
                                .withDescription(error.getMessage())
                                .asRuntimeException())
                );
    }

    @Override
    public void removeTransaction(final TransactionRemoveRequest request, final StreamObserver<TransactionResponse> responseObserver) {
        service.removeTransaction(request)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> responseObserver.onError(Status.INTERNAL
                                .withDescription(error.getMessage())
                                .asRuntimeException())
                );
    }

    @Override
    public void getTransactions(final TransactionGetRequest request, final StreamObserver<TransactionGetResponse> responseObserver) {
        service.getTransactions(request)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> responseObserver.onError(Status.INTERNAL
                                .withDescription(error.getMessage())
                                .asRuntimeException())
                );
    }
}