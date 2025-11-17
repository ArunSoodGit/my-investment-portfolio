package com.sood.application.portfolio.update;

import com.sood.infrastructure.entity.PortfolioEntity;
import sood.found.TransactionCreatedEvent;

/**
 * Strategy interface for updating portfolios based on transaction events.
 * Different implementations handle new items vs updates to existing items.
 */
public interface PortfolioUpdateStrategy {

    /**
     * Updates a portfolio based on a transaction event.
     *
     * @param portfolio the portfolio entity to update
     * @param event the transaction event triggering the update
     */
    void update(PortfolioEntity portfolio, TransactionCreatedEvent event);
}
