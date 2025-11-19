package com.sood.application.portfolio.item;

import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

import static com.sood.application.CalculatorHelper.calculateTransactionValue;

@Singleton
public class PortfolioItemFactory {

    public PortfolioItemEntity create(final TransactionCreatedEvent event) {
        PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol(event.symbol());
        item.setQuantity(event.quantity());
        item.setAveragePurchasePrice(event.price());
        item.setInvestedValue(calculateTransactionValue(event));
        item.setLastUpdated(event.date());
        return item;
    }
}