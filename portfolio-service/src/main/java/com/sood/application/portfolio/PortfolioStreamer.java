package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioResponse;
import com.sood.cache.PortfolioCacheManager;
import com.sood.infrastructure.entity.PortfolioEntity;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class PortfolioStreamer {

    private final PortfolioProcessor processor;
    private final PortfolioCacheManager portfolioCacheManager;

    public PortfolioStreamer(final PortfolioProcessor processor, final PortfolioCacheManager portfolioCacheManager) {
        this.processor = processor;
        this.portfolioCacheManager = portfolioCacheManager;
    }

    public Observable<PortfolioResponse> getPortfolioStream(final Long portfolioId) {
        final PortfolioEntity portfolio = portfolioCacheManager.get(portfolioId);

        return PortfolioEventPublisher.getObservable(portfolio.getId())
                .startWithItem(portfolio)
                .flatMapSingle(processor::process)
                .doOnError(error -> log.error("Błąd w streamPortfolio: {}", error.getMessage(), error))
                .doOnSubscribe(disposable -> log.info("Subskrypcja portfolio dla portfolioId {} rozpoczęta", portfolioId));
    }
}