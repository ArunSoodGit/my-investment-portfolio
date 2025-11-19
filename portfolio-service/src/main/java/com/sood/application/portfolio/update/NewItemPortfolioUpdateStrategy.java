package com.sood.application.portfolio.update;

import com.sood.application.portfolio.item.PortfolioItemFactory;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

/**
 * Adds new portfolio items to a portfolio based on buy transactions.
 * Ignores sell transactions for non-existing positions and emits update events.
 */
@Singleton
@Log4j2
public class NewItemPortfolioUpdateStrategy implements PortfolioUpdateStrategy {

    private final PortfolioItemFactory itemFactory;

    public NewItemPortfolioUpdateStrategy(final PortfolioItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    /**
     * Creates and adds a new portfolio item for buy transactions.
     * Ignores sell transactions for non-existing positions.
     * Persists changes and emits update events.
     *
     * @param portfolio the portfolio entity
     * @param event     the transaction event
     */
    @Override
    public void update(final PortfolioEntity portfolio, final TransactionCreatedEvent event) {

        if (isBuyTransaction(event)) {
            final PortfolioItemEntity created = itemFactory.createItem(event);
            portfolio.addItem(created);
        }
    }

    private boolean isBuyTransaction(final TransactionCreatedEvent event) {
        return TransactionType.BUY == event.type();
    }
}
