package com.sood.application.portfolio.update.strategy;

import com.sood.application.portfolio.update.PortfolioItemDomainService;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

@Singleton
public class SellUpdateStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemDomainService service;

    public SellUpdateStrategy(PortfolioItemDomainService service) {
        this.service = service;
    }

    @Override
    public void update(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        final PortfolioItemEntity itemEntity = portfolio.findItem(event.symbol())
                .orElseThrow(() -> new IllegalStateException("Cannot sell non-existing position"));
        final SellTransactionResponse outcome = service.handleSellTransaction(itemEntity, event);
        final PortfolioItemEntity updatedItemEntity = outcome.item();

        switch (outcome.result()) {
            case POSITION_UPDATED -> portfolio.addItem(updatedItemEntity);
            case POSITION_CLOSED -> portfolio.removeItem(updatedItemEntity);
        }
    }
}