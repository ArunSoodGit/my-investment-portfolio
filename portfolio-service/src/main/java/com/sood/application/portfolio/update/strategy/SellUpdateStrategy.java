package com.sood.application.portfolio.update.strategy;

import com.sood.application.portfolio.update.PortfolioItemCalculator;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;

@Singleton
public class SellUpdateStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemCalculator calculator;

    public SellUpdateStrategy(PortfolioItemCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void update(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {
        final PortfolioItemEntity item = portfolio.findItem(event.symbol())
                .orElseThrow(() -> new IllegalStateException("Cannot sell non-existing position"));
        final PortfolioItemEntity updated = calculator.updateForSell(item, event);

        if (updated == null) {
            portfolio.removeItem(item);
            return;
        }

        portfolio.addItem(updated);
    }
}