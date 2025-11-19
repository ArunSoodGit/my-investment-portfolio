package com.sood.application.portfolio.item;

import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.PortfolioItem;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;

import static com.sood.application.portfolio.util.PriceUtils.format;
import static com.sood.application.portfolio.util.PriceUtils.parsePrice;

/**
 * Builds gRPC portfolio item responses from portfolio item entities and market data.
 * Enriches portfolio items with calculated metrics and current market information.
 */
@Singleton
public class PortfolioItemBuilder {

    private final PortfolioItemSummaryBuilder summaryBuilder;

    public PortfolioItemBuilder(final PortfolioItemSummaryBuilder summaryBuilder) {
        this.summaryBuilder = summaryBuilder;
    }

    /**
     * Builds a gRPC PortfolioItem from a portfolio item entity and market data.
     * Calculates metrics like profit and includes current market information.
     *
     * @param entity     the portfolio item entity
     * @param marketData the current market data for the stock
     * @return the constructed portfolio item response
     */
    public PortfolioItem build(final PortfolioItemEntity entity, final MarketDataResponse marketData) {
        final BigDecimal currentPrice = parsePrice(marketData.getPrice());
        final PortfolioItemSummary itemSummary = summaryBuilder.build(entity, currentPrice);

        return PortfolioItem.newBuilder()
                .setSymbol(entity.getSymbol())
                .setName(marketData.getCompanyName())
                .setExchange(marketData.getExchange())
                .setCurrentPrice(format(currentPrice))
                .setAveragePurchasePrice(format(itemSummary.averagePurchasePrice()))
                .setTotalValue(format(itemSummary.totalValue()))
                .setQuantity(itemSummary.totalQuantity())
                .setProfit(format(itemSummary.profitValue()))
                .setProfitPercentage(itemSummary.profitPercentage())
                .setPercentageChange(marketData.getPercentageChange())
                .build();
    }
}
