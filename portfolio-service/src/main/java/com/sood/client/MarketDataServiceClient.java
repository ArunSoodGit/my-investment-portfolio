package com.sood.client;


import com.example.market.grpc.MarketDataRequest;
import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.MarketDataServiceGrpc;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;

/**
 * Client for fetching market data from the market data service.
 * Provides reactive access to stock price and market information.
 */
@Singleton
public class MarketDataServiceClient {

    private final MarketDataServiceGrpc.MarketDataServiceFutureStub stub;

    public MarketDataServiceClient(@GrpcChannel("market-data") io.grpc.Channel channel) {
        this.stub = MarketDataServiceGrpc.newFutureStub(channel);
    }

    /**
     * Fetches market data for a given stock symbol reactively.
     *
     * @param symbol the stock symbol
     * @return Single containing the market data response
     */
    public Single<MarketDataResponse> getMarketData(final String symbol) {
        final MarketDataRequest request = MarketDataRequest.newBuilder()
                .setSymbol(symbol)
                .build();

        return Single.fromFuture(stub.getMarketData(request));
    }
}