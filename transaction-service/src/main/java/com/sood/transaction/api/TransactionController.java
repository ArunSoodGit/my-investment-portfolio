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
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;

@GrpcService
public class TransactionController extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final TransactionManager service;

    @Inject
    public TransactionController(final TransactionManager service) {
        this.service = service;
    }

    @Override
    public void addTransaction(final TransactionAddRequest request,
            final StreamObserver<TransactionResponse> responseObserver) {
        handleSingle(service.addTransaction(request), responseObserver);
    }

    @Override
    public void removeTransaction(final TransactionRemoveRequest request,
            final StreamObserver<TransactionResponse> responseObserver) {
        handleSingle(service.removeTransaction(request), responseObserver);
    }

    @Override
    public void getTransactions(final TransactionGetRequest request,
            final StreamObserver<TransactionGetResponse> responseObserver) {
        handleSingle(service.getTransactions(request), responseObserver);
    }

    private <T> void handleSingle(final Single<T> single, final StreamObserver<T> observer) {
        single.subscribe(
                value -> {
                    observer.onNext(value);
                    observer.onCompleted();
                },
                error -> observer.onError(Status.INTERNAL.withDescription(error.getMessage()).asRuntimeException())
        );
    }
}