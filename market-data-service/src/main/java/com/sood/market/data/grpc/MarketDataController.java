package com.sood.market.data.grpc;

import com.example.market.grpc.MarketDataRequest;
import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.MarketDataServiceGrpc;
import com.sood.market.data.service.MarketDataService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class MarketDataController extends MarketDataServiceGrpc.MarketDataServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(MarketDataController.class);

    private final MarketDataService service;

    public MarketDataController(final MarketDataService service) {
        this.service = service;
    }

    @Override
    public void getMarketData(final MarketDataRequest request, final StreamObserver<MarketDataResponse> responseObserver) {
        final String symbol = request.getSymbol();
        service.getMarketData(symbol)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleError(symbol, error, responseObserver)
                );
    }

    private void handleError(final String symbol, final Throwable error, final StreamObserver<MarketDataResponse> observer) {
        log.error("Błąd podczas pobierania danych dla {}: {}", symbol, error.getMessage(), error);
        observer.onError(
                Status.INTERNAL
                        .withDescription("Błąd podczas pobierania danych")
                        .augmentDescription(error.getMessage())
                        .asRuntimeException()
        );
    }
}