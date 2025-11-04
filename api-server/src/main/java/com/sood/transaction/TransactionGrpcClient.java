package com.sood.transaction;

import com.example.market.grpc.TransactionAddRequest;
import com.example.market.grpc.TransactionGetRequest;
import com.example.market.grpc.TransactionGetResponse;
import com.example.market.grpc.TransactionRemoveRequest;
import com.example.market.grpc.TransactionResponse;
import com.example.market.grpc.TransactionServiceGrpc;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;

@Singleton
public class TransactionGrpcClient {

    private final TransactionServiceGrpc.TransactionServiceStub stub;

    public TransactionGrpcClient(@GrpcChannel("transaction") final io.grpc.Channel channel) {
        this.stub = TransactionServiceGrpc.newStub(channel);
    }

    public Single<TransactionGetResponse> getTransactions(final Long portfolioId, final String symbol) {
        final TransactionGetRequest request = TransactionGetRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .setSymbol(symbol)
                .build();
        return Single.create(emitter ->
                stub.getTransactions(request, new io.grpc.stub.StreamObserver<>() {
                    @Override
                    public void onNext(final TransactionGetResponse value) {
                        emitter.onSuccess(value);
                    }

                    @Override
                    public void onError(final Throwable t) {
                        emitter.onError(t);
                    }

                    @Override
                    public void onCompleted() {
                    }
                })
        );
    }

    public Single<TransactionResponse> addTransaction(final Long portfolioId, final TransactionRequest transactionRequest) {
        final TransactionAddRequest request = TransactionAddRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .setSymbol(transactionRequest.getSymbol())
                .setQuantity(transactionRequest.getQuantity())
                .setPrice(transactionRequest.getPurchasePrice())
                .setDate(transactionRequest.getDate())
                .build();

        return Single.create(emitter ->
                stub.addTransaction(request, new io.grpc.stub.StreamObserver<>() {
                    @Override
                    public void onNext(final TransactionResponse value) {
                        emitter.onSuccess(value);
                    }

                    @Override
                    public void onError(final Throwable t) {
                        emitter.onError(t);
                    }

                    @Override
                    public void onCompleted() {
                    }
                })
        );
    }

    public Single<TransactionResponse> removeTransaction(final Long transactionId) {
        final TransactionRemoveRequest request = TransactionRemoveRequest.newBuilder()
                .setTransactionId(transactionId)
                .build();
        return Single.create(emitter ->
                stub.removeTransaction(request, new io.grpc.stub.StreamObserver<>() {
                    @Override
                    public void onNext(TransactionResponse value) {
                        emitter.onSuccess(value);
                    }

                    @Override
                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    @Override
                    public void onCompleted() {
                    }
                })
        );
    }
}
