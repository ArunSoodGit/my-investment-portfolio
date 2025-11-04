package com.sood.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.example.market.grpc.PortfolioResponse;
import com.example.market.grpc.TransactionInfo;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sood.found.TransactionCreatedEvent;

@Singleton
public class PortfolioCalculator {

    public PortfolioResponse recalculate(final PortfolioResponse oldPortfolio, final TransactionCreatedEvent event) {

        // Mapujemy istniejące elementy i dodajemy zaktualizowany/nowy element dla event.symbol()
        final Map<String, PortfolioItem> updatedMap = oldPortfolio.getItemsList().stream()
                .collect(Collectors.toMap(
                        PortfolioItem::getSymbol,
                        item -> {
                            return updatePortfolioItemWithTransaction(event, item);
                        }
                ));

        // jeśli symbol nie istnieje w mapie, dodajemy nowy element
        updatedMap.computeIfAbsent(event.symbol(), symbol -> PortfolioItem.newBuilder()
                .setSymbol(symbol)
                .setQuantity(event.quantity())
                .setPurchasePrice(event.price())
                .setCurrentPrice(event.price())
                .setTotalValue(event.quantity() * event.price())
                .setProfit(0)
                .addBuyTransactions(TransactionInfo.newBuilder()
                        .setDate(event.date().toString())
                        .setPurchasePrice(event.price())
                        .setQuantity(event.quantity())
                        .build())
                .build());

        final List<PortfolioItem> updatedItems = List.copyOf(updatedMap.values());

        final double totalCurrentValue = updatedItems.stream().mapToDouble(PortfolioItem::getTotalValue).sum();
        final double totalInvested = updatedItems.stream()
                .flatMap(item -> item.getBuyTransactionsList().stream())
                .mapToDouble(tx -> tx.getPurchasePrice() * tx.getQuantity())
                .sum();
        final  double totalProfit = updatedItems.stream().mapToDouble(PortfolioItem::getProfit).sum();

        return PortfolioResponse.newBuilder()
                .addAllItems(updatedItems)
                .setUserId(oldPortfolio.getUserId())
                .setTotalCurrentValue(totalCurrentValue)
                .setTotalInvestedValue(totalInvested)
                .setTotalProfitValue(totalProfit)
                .setLastUpdated(LocalDate.now().toString())
                .build();
    }

    private static PortfolioItem updatePortfolioItemWithTransaction(final TransactionCreatedEvent event, final PortfolioItem item) {
        if (item.getSymbol().equals(event.symbol())) {
            final List<TransactionInfo> updatedTransactions = Stream.concat(
                    item.getBuyTransactionsList().stream(),
                    Stream.of(TransactionInfo.newBuilder()
                            .setDate(event.date().toString())
                            .setPurchasePrice(event.price())
                            .setQuantity(event.quantity())
                            .build())
            ).toList();

            final double totalQuantity = updatedTransactions.stream().mapToDouble(TransactionInfo::getQuantity).sum();
            final double totalInvested = updatedTransactions.stream().mapToDouble(tx -> tx.getPurchasePrice() * tx.getQuantity()).sum();
            final double averagePrice = totalQuantity > 0 ? totalInvested / totalQuantity : 0.0;

            return PortfolioItem.newBuilder()
                    .setSymbol(item.getSymbol())
                    .setQuantity(totalQuantity)
                    .setPurchasePrice(averagePrice)
                    .setCurrentPrice(event.price())
                    .setTotalValue(totalQuantity * event.price())
                    .setProfit(totalQuantity * event.price() - totalInvested)
                    .addAllBuyTransactions(updatedTransactions)
                    .build();
        } else {
            return item;
        }
    }
}