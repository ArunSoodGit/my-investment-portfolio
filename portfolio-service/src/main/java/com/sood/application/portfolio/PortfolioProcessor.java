package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioResponse;
import com.sood.application.portfolio.item.ItemProcessor;
import com.sood.infrastructure.entity.PortfolioEntity;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;

/**
 * Processes portfolio entities into gRPC response objects.
 * Transforms portfolio data by processing each portfolio item and building the response.
 */
@Singleton
public class PortfolioProcessor {

    private final ItemProcessor itemProcessor;
    private final PortfolioResponseBuilder responseBuilder;

    public PortfolioProcessor(final ItemProcessor itemProcessor, final PortfolioResponseBuilder responseBuilder) {
        this.itemProcessor = itemProcessor;
        this.responseBuilder = responseBuilder;
    }

    public Single<PortfolioResponse> process(final PortfolioEntity portfolio) {
        return Observable.fromIterable(portfolio.getItems())
                .concatMapSingle(itemProcessor::process)
                .toList()
                .map(portfolioItems -> responseBuilder.build(portfolio, portfolioItems));
    }
}