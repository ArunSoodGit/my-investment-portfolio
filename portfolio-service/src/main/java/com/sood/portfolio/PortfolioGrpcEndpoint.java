package com.sood.portfolio;

import com.example.market.grpc.PortfolioRequest;
import com.example.market.grpc.PortfolioResponse;
import com.example.market.grpc.PortfolioServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Inject;

@GrpcService
public class PortfolioGrpcEndpoint extends PortfolioServiceGrpc.PortfolioServiceImplBase {

    @Inject
    PortfolioService service;

    @Override
    public void getPortfolio(final PortfolioRequest request, final StreamObserver<PortfolioResponse> responseObserver) {
        try {
            final PortfolioResponse response = service.getPortfolio(request.getUserId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
