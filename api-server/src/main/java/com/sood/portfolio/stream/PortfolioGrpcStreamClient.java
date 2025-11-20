package com.sood.portfolio.stream;

import com.example.market.grpc.PortfolioRequest;
import com.example.market.grpc.PortfolioResponse;
import com.example.market.grpc.PortfolioServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioGrpcStreamClient {

    private final PortfolioServiceGrpc.PortfolioServiceStub stub;
    private final StreamEventLogger streamEventLogger;
    private final PortfolioStreamRetryPolicy retryPolicy;
    private final PortfolioRequestBuilder requestBuilder;

    public PortfolioGrpcStreamClient(@GrpcChannel("portfolio") final io.grpc.Channel channel) {
        this.stub = PortfolioServiceGrpc.newStub(channel);
        this.streamEventLogger = new DefaultStreamEventLogger();
        this.retryPolicy = new PortfolioStreamRetryPolicy();
        this.requestBuilder = new PortfolioRequestBuilder();
    }

    public Observable<PortfolioResponse> stream(final Long portfolioId) {
        return Observable.<PortfolioResponse>create(emitter ->
                        initiatePortfolioStream(emitter, portfolioId))
                .doOnSubscribe(disposable -> streamEventLogger.logStreamStarted(portfolioId))
                .retryWhen(errors ->
                        errors.delay(retryPolicy.getRetryDelaySeconds(), retryPolicy.getRetryTimeUnit())
                                .doOnNext(e -> streamEventLogger.logRetryAttempt(portfolioId))
                );
    }

    private void initiatePortfolioStream(final ObservableEmitter<PortfolioResponse> emitter, final Long portfolioId) {
        final StreamObserver<PortfolioResponse> responseObserver =
                new PortfolioStreamObserver(portfolioId, emitter, streamEventLogger);

        final PortfolioRequest request = requestBuilder.build(portfolioId);
        stub.streamPortfolio(request, responseObserver);

        emitter.setCancellable(() -> streamEventLogger.logStreamCancelled(portfolioId));
    }
}