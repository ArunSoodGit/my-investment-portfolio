package com.sood.transaction.infrastructure.adapter;

import com.example.market.grpc.MarketDataRequest;
import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.MarketDataServiceGrpc;
import com.sood.transaction.domain.port.MarketDataPort;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;

@Singleton
public class MarketDataClientAdapter implements MarketDataPort {

    private final MarketDataServiceGrpc.MarketDataServiceFutureStub stub;

    public MarketDataClientAdapter(@GrpcChannel("market-data") final io.grpc.Channel channel) {
        this.stub = MarketDataServiceGrpc.newFutureStub(channel);
    }

    @Override
    public Single<MarketDataResponse> getMarketData(final String symbol) {
        final MarketDataRequest request = MarketDataRequest.newBuilder().setSymbol(symbol).build();
        return Single.fromFuture(stub.getMarketData(request));
    }
}
