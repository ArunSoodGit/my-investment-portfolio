package com.sood.portfolio;

import com.example.market.grpc.PortfolioRequest;
import com.example.market.grpc.PortfolioResponse;
import com.example.market.grpc.PortfolioServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Singleton;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class PortfolioGrpcClient {

    private final PortfolioServiceGrpc.PortfolioServiceStub stub;

    public PortfolioGrpcClient(@GrpcChannel("portfolio") final io.grpc.Channel channel) {
        this.stub = PortfolioServiceGrpc.newStub(channel);
    }

    /**
     * Subskrybuje strumień PortfolioResponse dla danego portfolioId.
     * Zwraca Flowable, który reaguje na dane w czasie rzeczywistym.
     */
    public Flowable<PortfolioResponse> streamPortfolio(final Long portfolioId) {
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        return Flowable.<PortfolioResponse>create(emitter -> {
                    log.info("Rozpoczynam subskrypcję strumienia portfolio dla portfolioId={}", portfolioId);

                    final StreamObserver<PortfolioResponse> responseObserver = new StreamObserver<>() {
                        @Override
                        public void onNext(final PortfolioResponse response) {
                            if (!emitter.isCancelled()) {
                                emitter.onNext(response);
                            }
                        }

                        @Override
                        public void onError(final Throwable t) {
                            log.error("Błąd w strumieniu portfolio portfolioId={}: {}", portfolioId, t.getMessage(), t);
                            if (!emitter.isCancelled()) {
                                emitter.onError(t);
                            }
                        }

                        @Override
                        public void onCompleted() {
                            log.info("Serwer zakończył stream (nie powinno się zdarzyć w normalnej pracy).");
                        }
                    };

                    stub.streamPortfolio(request, responseObserver);
                    emitter.setCancellable(() -> log.info("Anulowano subskrypcję strumienia portfolio dla portfolioId={}", portfolioId));

                }, BackpressureStrategy.BUFFER)
                .doOnSubscribe(s -> log.debug("Subskrypcja aktywna dla portfolioId={}", portfolioId))
                .doOnNext(resp -> log.trace("Otrzymano update portfolio {} ({} pozycji)", resp.getPortfolioId(), resp.getItemsCount()))
                .doOnError(err -> log.warn("Strumień portfolio przerwany: {}", err.getMessage()))
                .retryWhen(errors ->
                        errors.delay(5, TimeUnit.SECONDS)
                                .doOnNext(e -> log.warn("Ponawiam połączenie do streamPortfolio po błędzie..."))
                ); // automatyczny reconnect
    }
}
