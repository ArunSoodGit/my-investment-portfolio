package com.sood.application.portfolio.item;

import com.example.market.grpc.PortfolioItem;
import com.sood.client.MarketDataServiceClient;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;

@Singleton
public class ItemProcessor {

    private final MarketDataServiceClient client;
    private final PortfolioItemBuilder itemBuilder;

    public ItemProcessor(final MarketDataServiceClient client, final PortfolioItemBuilder itemBuilder) {
        this.client = client;
        this.itemBuilder = itemBuilder;
    }

    public Single<PortfolioItem> process(final PortfolioItemEntity itemEntity) {
        return client.getMarketData(itemEntity.getSymbol())
                .map(marketData -> itemBuilder.build(itemEntity, marketData));
    }
}