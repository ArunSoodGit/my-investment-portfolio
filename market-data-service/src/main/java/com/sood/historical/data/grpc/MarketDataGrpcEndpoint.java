package com.sood.historical.data.grpc;

import com.example.market.grpc.DataRequest;
import com.example.market.grpc.DataResponse;
import com.example.market.grpc.MarketServiceGrpc;
import com.sood.historical.data.service.MarketDataService;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Inject;

@GrpcService
public class MarketDataGrpcEndpoint extends MarketServiceGrpc.MarketServiceImplBase {

    @Inject
    MarketDataService service;

    @Override
    public void getData(DataRequest request, StreamObserver<DataResponse> responseObserver) {
        service.getData(request.getSymbol(), request.getInterval())
                .subscribe(
                        dataResponse -> {
                            responseObserver.onNext(dataResponse);
                            responseObserver.onCompleted();
                        },
                        responseObserver::onError
                );
    }
}