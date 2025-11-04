package com.sood.transaction.application;

import com.example.market.grpc.TransactionAddRequest;
import com.example.market.grpc.TransactionGetRequest;
import com.example.market.grpc.TransactionGetResponse;
import com.example.market.grpc.TransactionRemoveRequest;
import com.example.market.grpc.TransactionResponse;
import com.sood.transaction.client.MarketDataClient;
import com.sood.transaction.infrastructure.entity.TransactionEntity;
import com.sood.transaction.infrastructure.service.TransactionService;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import java.util.List;
import sood.found.TransactionType;

@Singleton
public class TransactionManager {

    private final TransactionProcessor processor;
    private final TransactionResponseFactory builder;
    private final MarketDataClient client;
    private final TransactionMapper mapper;
    private final TransactionService service;

    public TransactionManager(final TransactionProcessor processor, final TransactionResponseFactory builder,
            final MarketDataClient client, final TransactionMapper mapper, final TransactionService service) {
        this.processor = processor;
        this.builder = builder;
        this.client = client;
        this.mapper = mapper;
        this.service = service;
    }

    public Single<TransactionResponse> addTransaction(final TransactionAddRequest request) {
        return Single.fromCallable(() -> {
            final TransactionEntity entity = mapper.mapToEntity(request);
            return processor.process(entity, TransactionType.BUY);
        });
    }

    public Single<TransactionResponse> removeTransaction(final TransactionRemoveRequest request) {
        return Single.fromCallable(() -> {
            final Long transactionId = request.getTransactionId();
            final TransactionEntity entity = service.findByTransactionId(transactionId);
            return processor.process(entity, TransactionType.SELL);
        });
    }

    public Single<TransactionGetResponse> getTransactions(final TransactionGetRequest request) {
        final String symbol = request.getSymbol();
        final List<TransactionEntity> entities = service.findByPortfolioIdAndSymbol(
                request.getPortfolioId(), symbol);

        return client.getStockDataReactive(symbol)
                .map(stockData -> builder.create(stockData, entities));
    }
}