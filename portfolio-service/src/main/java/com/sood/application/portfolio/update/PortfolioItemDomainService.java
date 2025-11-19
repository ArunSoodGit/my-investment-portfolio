package com.sood.application.portfolio.update;

import com.sood.application.portfolio.update.strategy.SellTransactionResponse;
import com.sood.application.portfolio.update.strategy.SellTransactionResult;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import sood.found.TransactionCreatedEvent;

import static com.sood.application.CalculatorHelper.calculateTransactionValue;

@Singleton
public class PortfolioItemDomainService {

    public PortfolioItemEntity handleBuyTransaction(final PortfolioItemEntity itemEntity, final TransactionCreatedEvent event) {
        final double oldQuantity = itemEntity.getQuantity();
        final BigDecimal oldInvestedValue = itemEntity.getInvestedValue();

        final double newQuantity = oldQuantity + event.quantity();
        final BigDecimal newInvestedValue = oldInvestedValue.add(calculateTransactionValue(event));

        return apply(itemEntity, newQuantity, newInvestedValue, event.date());
    }

    public SellTransactionResponse handleSellTransaction(final PortfolioItemEntity itemEntity, final TransactionCreatedEvent event) {
        final double oldQuantity = itemEntity.getQuantity();
        final double newQuantity = oldQuantity - event.quantity();

        if (positionIsClosed(newQuantity)) {
            return new SellTransactionResponse(SellTransactionResult.POSITION_CLOSED, itemEntity);
        }

        final BigDecimal proportion = BigDecimal.valueOf(newQuantity / oldQuantity);
        final BigDecimal newInvestedValue = itemEntity.getInvestedValue().multiply(proportion);
        final PortfolioItemEntity updatedItemEntity = apply(itemEntity, newQuantity, newInvestedValue, event.date());
        return new SellTransactionResponse(SellTransactionResult.POSITION_UPDATED, updatedItemEntity);
    }

    private PortfolioItemEntity apply(final PortfolioItemEntity item, final double newQuantity,
            final BigDecimal newInvestedValue, final LocalDate date) {
        item.setQuantity(newQuantity);
        item.setAveragePurchasePrice(newInvestedValue.divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP));
        item.setInvestedValue(newInvestedValue);
        item.setLastUpdated(date);
        return item;
    }

    boolean positionIsClosed(final double newQuantity) {
        return newQuantity <= 0;
    }
}