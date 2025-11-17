package com.sood.application.portfolio.item;

import com.sood.infrastructure.entity.PortfolioItemEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortfolioItemFactoryTest {

    private PortfolioItemFactory factory;
    private final LocalDate now = LocalDate.now();

    @BeforeEach
    void setUp() {
        factory = new PortfolioItemFactory();
    }

    @Test
    void testCreateItemWithBuyTransaction() {
        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 10.0,
                new BigDecimal("150.00"), TransactionType.BUY, now
        );

        final PortfolioItemEntity item = factory.createItem(event);

        assertEquals("AAPL", item.getSymbol());
        assertEquals(10.0, item.getQuantity());
        assertEquals(new BigDecimal("150.00"), item.getAveragePurchasePrice());
        assertEquals(new BigDecimal("1500.000"), item.getInvestedValue());
    }

    @Test
    void testCreateItemWithSellTransaction() {
        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 5.0,
                new BigDecimal("160.00"), TransactionType.SELL, now
        );

        final PortfolioItemEntity item = factory.createItem(event);

        assertEquals("AAPL", item.getSymbol());
        assertEquals(0.0, item.getQuantity());
        assertEquals(new BigDecimal("160.00"), item.getAveragePurchasePrice());
    }

    @Test
    void testUpdateItemWithBuyTransaction() {
        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol("AAPL");
        item.setQuantity(10.0);
        item.setAveragePurchasePrice(new BigDecimal("150.00"));
        item.setInvestedValue(new BigDecimal("1500.00"));

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 5.0,
                new BigDecimal("160.00"), TransactionType.BUY, now
        );

        final PortfolioItemEntity updated = factory.updateItem(event, item);

        assertEquals(15.0, updated.getQuantity());
        assertEquals(new BigDecimal("153.33"), updated.getAveragePurchasePrice());
        assertEquals(new BigDecimal("2300.000"), updated.getInvestedValue());
    }

    @Test
    void testUpdateItemWithSellTransaction() {
        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol("AAPL");
        item.setQuantity(10.0);
        item.setAveragePurchasePrice(new BigDecimal("150.00"));
        item.setInvestedValue(new BigDecimal("1500.00"));

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 3.0,
                new BigDecimal("160.00"), TransactionType.SELL, now
        );

        final PortfolioItemEntity updated = factory.updateItem(event, item);

        assertEquals(7.0, updated.getQuantity());
        assertEquals(new BigDecimal("150.00"), updated.getAveragePurchasePrice());
        assertEquals(new BigDecimal("1050.00"), updated.getInvestedValue());
    }

    @Test
    void testUpdateItemWithSellTransactionCompleteClose() {
        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol("AAPL");
        item.setQuantity(5.0);
        item.setAveragePurchasePrice(new BigDecimal("150.00"));
        item.setInvestedValue(new BigDecimal("750.00"));

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 5.0,
                new BigDecimal("160.00"), TransactionType.SELL, now
        );

        final PortfolioItemEntity updated = factory.updateItem(event, item);

        assertEquals(0.0, updated.getQuantity());
        assertEquals(BigDecimal.ZERO, updated.getAveragePurchasePrice());
    }

    @Test
    void testUpdateItemWithSellTransactionMoreThanAvailable() {
        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol("AAPL");
        item.setQuantity(5.0);
        item.setAveragePurchasePrice(new BigDecimal("150.00"));
        item.setInvestedValue(new BigDecimal("750.00"));

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 10.0,
                new BigDecimal("160.00"), TransactionType.SELL, now
        );

        final PortfolioItemEntity updated = factory.updateItem(event, item);

        assertEquals(0.0, updated.getQuantity());
        assertEquals(BigDecimal.ZERO, updated.getAveragePurchasePrice());
    }

    @Test
    void testUpdateItemWithNullAveragePurchasePrice() {
        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol("AAPL");
        item.setQuantity(10.0);
        item.setAveragePurchasePrice(null);
        item.setInvestedValue(BigDecimal.ZERO);

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 5.0,
                new BigDecimal("160.00"), TransactionType.BUY, now
        );

        final PortfolioItemEntity updated = factory.updateItem(event, item);

        assertEquals(15.0, updated.getQuantity());
        assertEquals(new BigDecimal("53.33"), updated.getAveragePurchasePrice());
    }
}
