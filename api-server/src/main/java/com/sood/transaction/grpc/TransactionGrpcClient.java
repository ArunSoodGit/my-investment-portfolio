package com.sood.transaction.grpc;

import com.example.market.grpc.TransactionGetResponse;
import com.example.market.grpc.TransactionResponse;
import com.example.market.grpc.TransactionServiceGrpc;
import com.sood.transaction.GrpcSingleObserver;
import com.sood.transaction.TransactionClient;
import com.sood.transaction.TransactionValidator;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import jakarta.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class TransactionGrpcClient implements TransactionClient {

    private final TransactionServiceGrpc.TransactionServiceStub stub;
    private final TransactionValidator validator;
    private final TransactionGrpcRequestBuilder requestBuilder;

    private static final long TIMEOUT_SECONDS = 30;
    private static final int RETRY_COUNT = 3;

    public TransactionGrpcClient(@GrpcChannel("transaction") final io.grpc.Channel channel,
            final TransactionGrpcRequestBuilder requestBuilder, final TransactionValidator validator) {
        this.stub = TransactionServiceGrpc.newStub(channel);
        this.requestBuilder = requestBuilder;
        this.validator = validator;
    }

    @Override
    public Single<TransactionGetResponse> getTransactions(final Long portfolioId, final String symbol) {
        validator.validateGetTransactionsRequest(portfolioId, symbol);

        final var request = requestBuilder.buildGetRequest(portfolioId, symbol);

        return callGrpc(emitter ->
                stub.getTransactions(request, new GrpcSingleObserver<>(emitter)));
    }

    @Override
    public Single<TransactionResponse> addTransaction(final Long portfolioId, final TransactionGrpcRequest transactionGrpcRequest) {
        validator.validateAddTransactionRequest(portfolioId, transactionGrpcRequest);

        final var request = requestBuilder.buildAddRequest(portfolioId, transactionGrpcRequest);

        return callGrpc(emitter ->
                stub.addTransaction(request, new GrpcSingleObserver<>(emitter)));
    }

    @Override
    public Single<TransactionResponse> removeTransaction(final Long transactionId) {
        validator.validateRemoveTransactionRequest(transactionId);

        final var request = requestBuilder.buildRemoveRequest(transactionId);

        return callGrpc(emitter ->
                stub.removeTransaction(request, new GrpcSingleObserver<>(emitter)));
    }

    /**
     * Generic wrapper for all GRPC → Single conversions.
     * Applies timeout and retry logic consistently.
     */
    private <T> Single<T> callGrpc(final SingleOnSubscribe<T> call) {
        return Single.create(call)
                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retry(RETRY_COUNT);
    }
}
