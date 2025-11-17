package com.sood.application.portfolio.item;

import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

/**
 * Factory for creating and updating portfolio item entities.
 * Handles the complex calculations for average purchase price and invested value
 * when portfolio items are bought or sold.
 */
@Singleton
public class PortfolioItemFactory {

    /**
     * Creates a new portfolio item from a transaction event.
     * Initializes quantity and invested value based on transaction type.
     *
     * @param event the transaction created event
     * @return new portfolio item entity
     */
    public PortfolioItemEntity createItem(final TransactionCreatedEvent event) {
        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol(event.symbol());
        item.setQuantity(event.type() == TransactionType.BUY ? event.quantity() : 0.0);
        item.setAveragePurchasePrice(event.price());
        item.setInvestedValue(event.price().multiply(BigDecimal.valueOf(event.quantity())));
        item.setLastUpdated(event.date());
        return item;
    }

    /**
     * Updates an existing portfolio item based on a transaction.
     * For BUY transactions: increases quantity and recalculates average purchase price.
     * For SELL transactions: decreases quantity; removes position if quantity becomes <= 0.
     *
     * @param event the transaction event
     * @param item the portfolio item to update
     * @return the updated portfolio item
     */
    public PortfolioItemEntity updateItem(final TransactionCreatedEvent event, final PortfolioItemEntity item) {
        final double oldQuantity = item.getQuantity();
        final BigDecimal oldAvgPrice = item.getAveragePurchasePrice() != null ? item.getAveragePurchasePrice() : BigDecimal.ZERO;
        final BigDecimal oldInvestedValue = oldAvgPrice.multiply(BigDecimal.valueOf(oldQuantity));

        if (event.type() == TransactionType.BUY) {
            final double newQuantity = oldQuantity + event.quantity();
            final BigDecimal newInvestedValue = oldInvestedValue.add(event.price().multiply(BigDecimal.valueOf(event.quantity())));
            final BigDecimal newAvgPrice = newInvestedValue.divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);

            item.setQuantity(newQuantity);
            item.setAveragePurchasePrice(newAvgPrice);
            item.setInvestedValue(newInvestedValue);
        } else if (event.type() == TransactionType.SELL) {
            final double newQuantity = oldQuantity - event.quantity();
            if (newQuantity <= 0) {
                item.setQuantity(0.0);
                item.setAveragePurchasePrice(BigDecimal.ZERO);
            } else {
                final BigDecimal proportion = BigDecimal.valueOf(newQuantity / oldQuantity);
                final BigDecimal newInvestedValue = oldInvestedValue.multiply(proportion).setScale(2, RoundingMode.HALF_UP);
                final BigDecimal newAvgPrice = newInvestedValue.divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);

                item.setQuantity(newQuantity);
                item.setAveragePurchasePrice(newAvgPrice);
                item.setInvestedValue(newInvestedValue);
            }
        }

        item.setLastUpdated(event.date());
        return item;
    }
}