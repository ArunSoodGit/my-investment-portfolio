package com.sood.application.portfolio.update.strategy;

import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.application.portfolio.update.PortfolioItemCalculator;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

@Singleton
public class BuyStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemFactory factory;
    private final PortfolioItemCalculator calculator;

    public BuyStrategy(final PortfolioItemFactory factory, final PortfolioItemCalculator calculator) {
        this.factory = factory;
        this.calculator = calculator;
    }

    @Override
    public void update(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        final PortfolioItemEntity item = portfolio.findItem(event.symbol())
                .orElseGet(() -> factory.create(event));
        final PortfolioItemEntity updatedItem = calculator.updateForBuy(item, event);

        portfolio.addItem(updatedItem);
    }
}