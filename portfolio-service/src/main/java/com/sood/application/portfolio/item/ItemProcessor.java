package com.sood.application.portfolio.item;

import com.example.market.grpc.PortfolioItem;
import com.sood.client.MarketDataServiceClient;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;

/**
 * Processes portfolio items by enriching them with current market data.
 * Fetches the latest price information and builds complete portfolio item responses.
 */
@Singleton
public class ItemProcessor {

    private final MarketDataServiceClient client;
    private final PortfolioItemBuilder itemBuilder;

    public ItemProcessor(final MarketDataServiceClient client, final PortfolioItemBuilder itemBuilder) {
        this.client = client;
        this.itemBuilder = itemBuilder;
    }

    public Single<PortfolioItem> process(final PortfolioItemEntity portfolioItem) {
        return client.getMarketData(portfolioItem.getSymbol())
                .map(marketData -> itemBuilder.build(portfolioItem, marketData));
    }
}