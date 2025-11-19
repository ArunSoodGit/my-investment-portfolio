package com.sood.application.portfolio.update;

import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import sood.found.TransactionCreatedEvent;

@Singleton
public class PortfolioItemCalculator {

    public PortfolioItemEntity updateForBuy(final PortfolioItemEntity item, final TransactionCreatedEvent event) {
        final double oldQuantity = item.getQuantity();
        final BigDecimal oldInvestedValue = item.getInvestedValue();

        final double newQuantity = oldQuantity + event.quantity();
        final BigDecimal newInvestedValue = oldInvestedValue.add(event.price().multiply(BigDecimal.valueOf(event.quantity())));

        return apply(item, newQuantity, newInvestedValue, event.date());
    }

    public PortfolioItemEntity updateForSell(final PortfolioItemEntity item, final TransactionCreatedEvent event) {
        final double oldQuantity = item.getQuantity();
        final double newQuantity = oldQuantity - event.quantity();

        if (newQuantity <= 0) {
            return null;
        }

        final BigDecimal proportion = BigDecimal.valueOf(newQuantity / oldQuantity);
        final BigDecimal newInvestedValue = item.getInvestedValue().multiply(proportion);

        return apply(item, newQuantity, newInvestedValue, event.date());
    }

    private PortfolioItemEntity apply(final PortfolioItemEntity item, final double newQty, final BigDecimal newInv,
            final LocalDate date) {
        item.setQuantity(newQty);
        item.setAveragePurchasePrice(newInv.divide(BigDecimal.valueOf(newQty), 2, RoundingMode.HALF_UP));
        item.setInvestedValue(newInv);
        item.setLastUpdated(date);
        return item;
    }
}