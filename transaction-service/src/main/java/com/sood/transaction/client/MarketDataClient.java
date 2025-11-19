package com.sood.transaction.client;

import com.example.market.grpc.MarketDataRequest;
import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.MarketDataServiceGrpc;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;

@Singleton
public class MarketDataClient {

    private final MarketDataServiceGrpc.MarketDataServiceFutureStub stub;

    public MarketDataClient(@GrpcChannel("marketdata") io.grpc.Channel channel) {
        this.stub = MarketDataServiceGrpc.newFutureStub(channel);
    }

    /**
     * Reaktywne pobranie danych giełdowych dla danego symbolu.
     */
    public Single<MarketDataResponse> getMarketData(final String symbol) {
        final MarketDataRequest request = MarketDataRequest.newBuilder()
                .setSymbol(symbol)
                .build();

        return Single.fromFuture(stub.getMarketData(request));
    }
}