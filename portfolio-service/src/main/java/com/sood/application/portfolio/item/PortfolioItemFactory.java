package com.sood.application.portfolio.item;

import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import sood.found.TransactionCreatedEvent;


@Singleton
public class PortfolioItemFactory {

    public PortfolioItemEntity create(final TransactionCreatedEvent event) {
        PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol(event.symbol());
        item.setQuantity(event.quantity());
        item.setAveragePurchasePrice(event.price());
        item.setInvestedValue(event.price().multiply(BigDecimal.valueOf(event.quantity())));
        item.setLastUpdated(event.date());
        return item;
    }
}