package com.sood.application.portfolio.item;

import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

@Singleton
public class PortfolioItemFactory {

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
     * Aktualizuje istniejącą pozycję na podstawie nowej transakcji.
     * Dla BUY -> zwiększa ilość i przelicza średnią cenę zakupu.
     * Dla SELL -> zmniejsza ilość, usuwa pozycję jeśli ilość <= 0.
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