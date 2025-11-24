package com.sood.transaction.application;

import com.example.market.grpc.TransactionAddRequest;
import com.example.market.grpc.TransactionGetRequest;
import com.example.market.grpc.TransactionGetResponse;
import com.example.market.grpc.TransactionRemoveRequest;
import com.example.market.grpc.TransactionResponse;
import com.sood.transaction.domain.model.Transaction;
import com.sood.transaction.domain.port.MarketDataPort;
import com.sood.transaction.domain.port.TransactionPersistencePort;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import java.util.List;
import sood.found.TransactionType;

@Singleton
public class TransactionManager {

    private final TransactionProcessor processor;
    private final TransactionResponseFactory builder;
    private final MarketDataPort marketDataPort;
    private final TransactionMapper mapper;
    private final TransactionPersistencePort persistencePort;

    public TransactionManager(final TransactionProcessor processor, final TransactionResponseFactory responseFactory,
            final MarketDataPort marketDataPort, final TransactionMapper mapper,
            final TransactionPersistencePort persistencePort) {
        this.processor = processor;
        this.builder = responseFactory;
        this.marketDataPort = marketDataPort;
        this.mapper = mapper;
        this.persistencePort = persistencePort;
    }

    public Single<TransactionResponse> addTransaction(final TransactionAddRequest request) {
        return Single.fromCallable(() -> {
            final Transaction transaction = mapper.fromAddRequest(request, TransactionType.BUY);
            return processor.process(transaction, TransactionType.BUY);
        });
    }

    public Single<TransactionResponse> removeTransaction(final TransactionRemoveRequest request) {
        return Single.fromCallable(() -> {
            final Transaction transaction = persistencePort.findById(request.getTransactionId());
            return processor.process(transaction, TransactionType.SELL);
        });
    }

    public Single<TransactionGetResponse> getTransactions(final TransactionGetRequest request) {
        final List<Transaction> transactions = persistencePort.findAllByPortfolioIdAndSymbol(
                request.getPortfolioId(), request.getSymbol());

        return marketDataPort.getMarketData(request.getSymbol())
                .map(stockData -> builder.create(stockData, transactions));
    }
}