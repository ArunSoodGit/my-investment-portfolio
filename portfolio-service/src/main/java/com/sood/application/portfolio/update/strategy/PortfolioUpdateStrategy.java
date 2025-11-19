package com.sood.application.portfolio.update.strategy;

import com.sood.infrastructure.entity.PortfolioEntity;
import sood.found.TransactionCreatedEvent;

/**
 * Strategy interface for updating portfolios based on transaction events.
 * Different implementations handle new items vs updates to existing items.
 */
public interface PortfolioUpdateStrategy {

    void update(PortfolioEntity portfolio, TransactionCreatedEvent event);
}
