package com.sood.logo.grpc;

import com.example.market.grpc.LogoRequest;
import com.example.market.grpc.LogoResponse;
import com.example.market.grpc.LogoServiceGrpc;
import com.sood.logo.LogoService;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Inject;

@GrpcService
public class LogoGrpcEndpoint extends LogoServiceGrpc.LogoServiceImplBase  {

    @Inject
    LogoService service;

    @Override
    public void getLogo(LogoRequest request, StreamObserver<LogoResponse> responseObserver) {
        service.getLogo(request.getSymbol())
                .subscribe(
                        dataResponse -> {
                            responseObserver.onNext(dataResponse);
                            responseObserver.onCompleted();
                        },
                        responseObserver::onError
                );
    }
}
