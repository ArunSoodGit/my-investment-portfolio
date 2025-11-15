package com.sood.application.portfolio.item;

import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.PortfolioItem;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;

import static com.sood.application.portfolio.util.PriceUtils.format;
import static com.sood.application.portfolio.util.PriceUtils.parsePrice;

@Singleton
public class PortfolioItemBuilder {

    private final PortfolioItemCalculator itemCalculator;

    public PortfolioItemBuilder(final PortfolioItemCalculator itemCalculator) {
        this.itemCalculator = itemCalculator;
    }

    public PortfolioItem build(final PortfolioItemEntity entity, final MarketDataResponse marketData) {
        final BigDecimal currentPrice = parsePrice(marketData.getPrice());
        final PortfolioItemSummary itemSummary = itemCalculator.calculate(entity, currentPrice);

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
