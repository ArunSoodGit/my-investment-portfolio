package com.sood.client;

import com.example.market.grpc.HistoricalDataIem;
import com.example.market.grpc.HistoricalDataSaveRequest;
import com.example.market.grpc.HistoricalDataServiceGrpc;
import com.example.market.grpc.PortfolioResponse;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;
import java.time.LocalDate;

@Singleton
public class HistoricalDataServiceClient {

    private final HistoricalDataServiceGrpc.HistoricalDataServiceBlockingStub stub;

    public HistoricalDataServiceClient(@GrpcChannel("historical-data") final io.grpc.Channel channel) {
        this.stub = HistoricalDataServiceGrpc.newBlockingStub(channel);
    }

    public void save(final PortfolioResponse portfolioResponse) {
        final HistoricalDataSaveRequest request = HistoricalDataSaveRequest.newBuilder()
                .setPortfolioId(portfolioResponse.getPortfolioId())
                .setHistoricalDataItem(HistoricalDataIem.newBuilder()
                        .setDate(LocalDate.now().toString())
                        .setInvestedValue(portfolioResponse.getInvestedValue())
                        .setCurrentValue(portfolioResponse.getCurrentValue())
                        .build())
                .build();
        stub.saveHistoricalData(request);
    }
}
