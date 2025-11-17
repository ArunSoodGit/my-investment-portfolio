package com.sood.application.portfolio.update;

import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

import static org.junit.jupiter.api.Assertions.assertSame;

class PortfolioUpdateStrategyFactoryTest {

    private ExistingItemPortfolioUpdateStrategy existingItemStrategy;
    private NewItemPortfolioUpdateStrategy newItemStrategy;
    private PortfolioUpdateStrategyFactory factory;
    private final LocalDate now = LocalDate.now();

    @BeforeEach
    void setUp() {
        existingItemStrategy = new ExistingItemPortfolioUpdateStrategy(null, null, null);
        newItemStrategy = new NewItemPortfolioUpdateStrategy(null, null, null);
        factory = new PortfolioUpdateStrategyFactory(existingItemStrategy, newItemStrategy);
    }

    @Test
    void testGetStrategyForNewItem() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setItems(Set.of());

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 10.0, new BigDecimal("150.00"),
                TransactionType.BUY, now
        );

        final PortfolioUpdateStrategy strategy = factory.getStrategy(portfolio, event);

        assertSame(newItemStrategy, strategy);
    }

    @Test
    void testGetStrategyForExistingItem() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        final PortfolioItemEntity existingItem = new PortfolioItemEntity();
        existingItem.setSymbol("AAPL");

        final Set<PortfolioItemEntity> items = new HashSet<>();
        items.add(existingItem);
        portfolio.setItems(items);

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 5.0, new BigDecimal("155.00"),
                TransactionType.BUY, now
        );

        final PortfolioUpdateStrategy strategy = factory.getStrategy(portfolio, event);

        assertSame(existingItemStrategy, strategy);
    }

    @Test
    void testGetStrategyForDifferentSymbol() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        final PortfolioItemEntity existingItem = new PortfolioItemEntity();
        existingItem.setSymbol("GOOGL");

        final Set<PortfolioItemEntity> items = new HashSet<>();
        items.add(existingItem);
        portfolio.setItems(items);

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 5.0, new BigDecimal("155.00"),
                TransactionType.BUY, now
        );

        final PortfolioUpdateStrategy strategy = factory.getStrategy(portfolio, event);

        assertSame(newItemStrategy, strategy);
    }

    @Test
    void testGetStrategyForMultipleExistingItems() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        final PortfolioItemEntity item1 = new PortfolioItemEntity();
        item1.setSymbol("AAPL");

        final PortfolioItemEntity item2 = new PortfolioItemEntity();
        item2.setSymbol("GOOGL");

        final PortfolioItemEntity item3 = new PortfolioItemEntity();
        item3.setSymbol("MSFT");

        final Set<PortfolioItemEntity> items = new HashSet<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        portfolio.setItems(items);

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "GOOGL", 5.0,
                new BigDecimal("2800.00"), TransactionType.BUY, now
        );

        final PortfolioUpdateStrategy strategy = factory.getStrategy(portfolio, event);

        assertSame(existingItemStrategy, strategy);
    }

    @Test
    void testGetStrategyForSellTransaction() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        final PortfolioItemEntity existingItem = new PortfolioItemEntity();
        existingItem.setSymbol("AAPL");

        final Set<PortfolioItemEntity> items = new HashSet<>();
        items.add(existingItem);
        portfolio.setItems(items);

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 5.0,
                new BigDecimal("160.00"), TransactionType.SELL, now
        );

        final PortfolioUpdateStrategy strategy = factory.getStrategy(portfolio, event);

        assertSame(existingItemStrategy, strategy);
    }

    @Test
    void testGetStrategyForEmptyPortfolio() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setItems(Set.of());

        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "TSLA", 10.0, new BigDecimal("250.00"), TransactionType.BUY, now
        );

        final PortfolioUpdateStrategy strategy = factory.getStrategy(portfolio, event);

        assertSame(newItemStrategy, strategy);
    }
}
