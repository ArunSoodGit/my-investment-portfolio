package com.sood.historical.data.infrastructure.adapter;

import com.example.market.grpc.HistoricalDataGetRequest;
import com.example.market.grpc.HistoricalDataGetResponse;
import com.example.market.grpc.HistoricalDataServiceGrpc;
import com.sood.historical.data.application.command.HistoricalDataCommand;
import com.sood.historical.data.application.port.HistoricalDataPort;
import com.sood.historical.data.application.result.HistoricalDataResult;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;

@Singleton
public class HistoricalDataGrpcClientAdapter implements HistoricalDataPort {

    private final HistoricalDataServiceGrpc.HistoricalDataServiceBlockingStub stub;

    public HistoricalDataGrpcClientAdapter(@GrpcChannel("historical-data") final io.grpc.Channel channel) {
        this.stub = HistoricalDataServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public HistoricalDataResult getHistoricalData(final HistoricalDataCommand command) {
        final HistoricalDataGetRequest request = HistoricalDataGetRequest.newBuilder()
                .setPortfolioId(command.portfolioId())
                .build();
        final HistoricalDataGetResponse response = stub.getHistoricalData(request);
        return HistoricalDataResult.from(response);
    }
}
