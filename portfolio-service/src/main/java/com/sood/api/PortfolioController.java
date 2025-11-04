package com.sood.api;

import com.example.market.grpc.PortfolioRequest;
import com.example.market.grpc.PortfolioResponse;
import com.example.market.grpc.PortfolioServiceGrpc;
import com.sood.application.portfolio.PortfolioStreamer;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@GrpcService
public class PortfolioController extends PortfolioServiceGrpc.PortfolioServiceImplBase {

    private final PortfolioStreamer service;

    public PortfolioController(final PortfolioStreamer service) {
        this.service = service;
    }

    @Override
    public void streamPortfolio(final PortfolioRequest request, final StreamObserver<PortfolioResponse> responseObserver) {
        final long portfolioId = request.getPortfolioId();
        service.getPortfolioStream(portfolioId)
                .doOnNext(portfolioResponse -> log.info("Wysyłam portfel do klienta: portfolioId {}", portfolioResponse.getPortfolioId()))
                .doOnError(error -> log.info("Błąd podczas streamowania portfela {}: {}", portfolioId, error.getMessage()))
                .doFinally(() -> log.info("Zakończono subskrypcję dla portfolioId {}", portfolioId))
                .subscribe(responseObserver::onNext, error -> {
                    // nie kończymy strumienia, tylko logujemy
                    log.info("Błąd w subskrypcji gRPC: {}", error.getMessage());
                });
    }
}
