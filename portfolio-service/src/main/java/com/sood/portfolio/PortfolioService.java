package com.sood.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.example.market.grpc.PortfolioResponse;
import com.example.market.grpc.TransactionInfo;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.util.List;
import sood.found.TransactionCreatedEvent;

@Singleton
public class PortfolioService {

    private final PortfolioRepository repository;

    public PortfolioService(final PortfolioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PortfolioResponse getPortfolio(final String userId, final String foundName) {
        final PortfolioEntity portfolio = repository.findByUserIdAndFoundName(userId, foundName);
        final List<PortfolioItemEntity> portfolioItems = portfolio.getItems();
        final List<PortfolioItem> items = portfolioItems.stream()
                .map(PortfolioService::getBuild)
                .toList();
        final Double totalCurrentValue = portfolioItems.stream()
                .map(PortfolioItemEntity::getTotalValue)
                .reduce(Double::sum).orElse(0.0);
        final Double totalProfitValue = portfolioItems.stream()
                .map(PortfolioItemEntity::getProfitLoss)
                .reduce(Double::sum).orElse(0.0);

        return PortfolioResponse.newBuilder()
                .setUserId(userId)
                .addAllItems(items)
                .setLastUpdated(portfolio.getLastUpdated().toString())
                .setTotalCurrentValue(totalCurrentValue)
                .setTotalInvestedValue(0)
                .setTotalProfitValue(totalProfitValue)
                .build();
    }

    private static PortfolioItem getBuild(PortfolioItemEntity portfolioItemEntity) {
        return PortfolioItem.newBuilder()
                .setSymbol(portfolioItemEntity.getSymbol())
                .setQuantity(portfolioItemEntity.getQuantity())
                .setCurrentPrice(portfolioItemEntity.getCurrentPrice())
                .setTotalValue(portfolioItemEntity.getTotalValue())
                .setPurchaseDate(portfolioItemEntity.getLastUpdated().toString())
                .setProfit(portfolioItemEntity.getProfitLoss())
                .addAllBuyTransactions(getList(portfolioItemEntity))
                .build();
    }

    private static List<TransactionInfo> getList(PortfolioItemEntity portfolioItemEntity) {
        return portfolioItemEntity.getTransactions().stream()
                .map(portfolioItemTransactionEntity -> TransactionInfo.newBuilder()
                        .setPurchasePrice(portfolioItemTransactionEntity.getPrice())
                        .setDate(portfolioItemTransactionEntity.getTransactionDate().toString())
                        .setQuantity(portfolioItemTransactionEntity.getQuantity())
                        .build()).toList();
    }

    @Transactional
    public void updatePortfolio(final TransactionCreatedEvent event) {
        final PortfolioEntity portfolio = repository.findByUserIdAndFoundName(event.userId(), event.foundName());

        if (isStockInThisFound(event, portfolio)) {
            final PortfolioItemEntity item = portfolio.getItems().stream()
                    .filter(portfolioItemEntity -> portfolioItemEntity.getSymbol().equals(event.symbol()))
                    .findFirst()
                    .orElseThrow();

            if (event.type().equals("BUY")) {
                final PortfolioItemTransactionEntity newTransaction = new PortfolioItemTransactionEntity();
                newTransaction.setTransactionDate(event.date());
                newTransaction.setType(event.type());
                newTransaction.setPrice(event.price());
                newTransaction.setQuantity(event.quantity());
                item.addTransaction(newTransaction);
                item.setAveragePurchasePrice(calculate(item.getTransactions()));
            }
            portfolio.addItem(item);
        } else {
            final PortfolioItemTransactionEntity transaction = new PortfolioItemTransactionEntity();
            transaction.setTransactionDate(event.date());
            transaction.setType(event.type());
            transaction.setPrice(event.price());
            transaction.setQuantity(event.quantity());

            final PortfolioItemEntity item = new PortfolioItemEntity();
            item.setSymbol(event.symbol());
            item.setQuantity(event.quantity());
            item.setLastUpdated(event.date());
            item.setCurrentPrice(0.0);
            item.setAveragePurchasePrice(event.price());
            item.setTotalValue(event.quantity() * event.price());
            item.setProfitLoss(0.0);

            item.addTransaction(transaction);
            portfolio.addItem(item);
        }

        repository.update(portfolio);
    }

    private double calculate(List<PortfolioItemTransactionEntity> transactions) {
        return transactions.stream()
                .mapToDouble(PortfolioItemTransactionEntity::getPrice)
                .average()
                .orElse(0.0);
    }

    private boolean isStockInThisFound(final TransactionCreatedEvent event, final PortfolioEntity portfolio) {
        return portfolio.getItems().stream()
                .anyMatch(portfolioItemEntity -> portfolioItemEntity.getSymbol().equals(event.symbol()));
    }
}