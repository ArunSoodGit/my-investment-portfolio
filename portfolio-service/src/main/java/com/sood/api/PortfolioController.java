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

    /**
     * Streams portfolio data to the client.
     * Returns an infinite stream of portfolio updates starting with the current snapshot.
     *
     * @param request          the portfolio request containing the portfolio ID
     * @param responseObserver the observer for sending portfolio responses to the client
     */
    @Override
    public void streamPortfolio(final PortfolioRequest request, final StreamObserver<PortfolioResponse> responseObserver) {
        final long portfolioId = request.getPortfolioId();
        service.getPortfolioStream(portfolioId)
                .doOnNext(portfolioResponse -> log.debug("Sending portfolio to client: portfolioId {}", portfolioResponse.getPortfolioId()))
                .doOnError(error -> log.error("Error during portfolio streaming for portfolioId {}: {}", portfolioId, error.getMessage(), error))
                .subscribe(
                        responseObserver::onNext,
                        error -> {
                            log.error("Error in gRPC subscription for portfolioId {}", portfolioId, error);
                            responseObserver.onError(error);
                        }
                );
    }
}
