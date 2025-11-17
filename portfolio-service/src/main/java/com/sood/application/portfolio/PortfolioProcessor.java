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

    /**
     * Processes a portfolio entity and returns a Single with the gRPC response.
     * Each portfolio item is processed individually using the ItemProcessor.
     *
     * @param portfolio the portfolio entity to process
     * @return Single containing the processed portfolio response
     */
    public Single<PortfolioResponse> process(final PortfolioEntity portfolio) {
        return Observable.fromIterable(portfolio.getItems())
                .concatMapSingle(itemProcessor::process)
                .toList()
                .map(portfolioItems -> responseBuilder.build(portfolio, portfolioItems));
    }
}