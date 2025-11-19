package com.sood.application.portfolio.update.strategy;

import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.application.portfolio.update.PortfolioItemDomainService;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

@Singleton
public class BuyUpdateStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemFactory factory;
    private final PortfolioItemDomainService service;

    public BuyUpdateStrategy(final PortfolioItemFactory factory, final PortfolioItemDomainService calculator) {
        this.factory = factory;
        this.service = calculator;
    }

    @Override
    public void update(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        final PortfolioItemEntity item = portfolio.findItem(event.symbol())
                .orElseGet(() -> factory.create(event));
        final PortfolioItemEntity updatedItem = service.handleBuyTransaction(item, event);

        portfolio.addItem(updatedItem);
    }
}