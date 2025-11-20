package com.sood.portfolio.stream;

import com.example.market.grpc.PortfolioResponse;
import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class PortfolioStreamObserver implements StreamObserver<PortfolioResponse> {

    private final Long portfolioId;
    private final ObservableEmitter<PortfolioResponse> emitter;
    private final StreamEventLogger logger;

    public PortfolioStreamObserver(final Long portfolioId, final ObservableEmitter<PortfolioResponse> emitter,
            final StreamEventLogger logger) {
        this.portfolioId = portfolioId;
        this.emitter = emitter;
        this.logger = logger;
    }

    @Override
    public void onNext(final PortfolioResponse response) {
        if (!emitter.isDisposed()) {
            emitter.onNext(response);
        }
    }

    @Override
    public void onError(final Throwable t) {
        logger.logStreamError(portfolioId, t.getMessage());
        if (!emitter.isDisposed()) {
            emitter.onError(t);
        }
    }

    @Override
    public void onCompleted() {
        logger.logStreamCompleted(portfolioId);
        if (!emitter.isDisposed()) {
            emitter.onComplete();
        }
    }
}
