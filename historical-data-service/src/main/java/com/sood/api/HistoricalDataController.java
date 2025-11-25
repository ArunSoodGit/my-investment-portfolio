package com.sood.api;

import com.example.market.grpc.HistoricalDataGetRequest;
import com.example.market.grpc.HistoricalDataGetResponse;
import com.example.market.grpc.HistoricalDataSaveRequest;
import com.example.market.grpc.HistoricalDataServiceGrpc;
import com.google.protobuf.Empty;
import com.sood.application.HistoricalDataApplicationService;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;

@GrpcService
public class HistoricalDataController extends HistoricalDataServiceGrpc.HistoricalDataServiceImplBase {

    private final HistoricalDataApplicationService service;

    public HistoricalDataController(final HistoricalDataApplicationService service) {
        this.service = service;
    }

    @Override
    public void getHistoricalData(final HistoricalDataGetRequest request, final StreamObserver<HistoricalDataGetResponse> responseObserver) {
        try {
            final HistoricalDataGetResponse response = service.getHistoricalData(request.getPortfolioId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void saveHistoricalData(final HistoricalDataSaveRequest request, final StreamObserver<Empty> responseObserver) {
        service.saveHistoricalData(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
