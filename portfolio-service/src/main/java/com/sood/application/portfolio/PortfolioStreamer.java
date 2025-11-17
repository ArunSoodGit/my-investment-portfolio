package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioResponse;
import com.sood.application.portfolio.provider.PortfolioProvider;
import com.sood.infrastructure.entity.PortfolioEntity;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

/**
 * Provides reactive streams of portfolio data.
 * Combines initial portfolio snapshot with continuous event-driven updates.
 */
@Singleton
@Log4j2
public class PortfolioStreamer {

    private final PortfolioProcessor processor;
    private final PortfolioProvider provider;

    public PortfolioStreamer(final PortfolioProcessor processor, final PortfolioProvider provider) {
        this.processor = processor;
        this.provider = provider;
    }

    /**
     * Returns a reactive stream of portfolio updates.
     * Starts with the current portfolio snapshot and emits updates as portfolio events occur.
     *
     * @param portfolioId the portfolio identifier
     * @return Observable stream of portfolio responses
     */
    public Observable<PortfolioResponse> getPortfolioStream(final Long portfolioId) {
        final PortfolioEntity portfolio = provider.provide(portfolioId);

        return PortfolioEventPublisher.getObservable(portfolio.getId())
                .startWithItem(portfolio)
                .flatMapSingle(processor::process)
                .doOnError(error -> log.error("Error in portfolio stream: {}", error.getMessage(), error))
                .doOnSubscribe(disposable -> log.info("Portfolio subscription started for portfolioId {}", portfolioId));
    }
}