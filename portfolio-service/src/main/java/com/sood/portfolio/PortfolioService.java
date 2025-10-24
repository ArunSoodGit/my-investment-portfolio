package com.sood.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.example.market.grpc.PortfolioResponse;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class PortfolioService {

    public PortfolioResponse getPortfolio(final String userId) {
        final List<PortfolioItem> items = getItems();
        return PortfolioResponse.newBuilder()
                .setTotalCurrentValue(getTotalCurrentValue(items))
                .setTotalInvestedValue(getTotalInvestedValue(items))
                .setTotalProfitValue(getTotalProfitValue(items))
                .addAllItems(items)
                .build();
    }

    private double getTotalCurrentValue(final List<PortfolioItem> items) {
        return items.stream().mapToDouble(PortfolioItem::getTotalValue).sum();
    }

    private double getTotalInvestedValue(final List<PortfolioItem> items) {
        return items.stream().mapToDouble(PortfolioItem::getPurchasePrice).sum();
    }

    private double getTotalProfitValue(final List<PortfolioItem> items) {
        return items.stream().mapToDouble(PortfolioItem::getProfit).sum();
    }

    private List<PortfolioItem> getItems() {
        return List.of(PortfolioItem.newBuilder()
                        .setName("Apple")
                        .setCurrency("USD")
                        .setCurrentPrice(650.0)
                        .setPurchaseDate("2025-11-03")
                        .setTotalValue(1300.0)
                        .setPurchasePrice(620.0)
                        .setQuantity(2)
                        .setProfit(60)
                        .build(),
                PortfolioItem.newBuilder()
                        .setName("Microsoft")
                        .setCurrency("USD")
                        .setCurrentPrice(200)
                        .setTotalValue(600)
                        .setProfit(300)
                        .setPurchaseDate("2025-11-01")
                        .setPurchasePrice(100)
                        .setQuantity(3)
                        .build()
        );
    }
}
