package com.sood.application.portfolio.item;

import com.sood.infrastructure.entity.PortfolioItemEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortfolioItemCalculatorTest {

    private PortfolioItemCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PortfolioItemCalculator();
    }

    @Test
    void testCalculateWithPositiveProfit() {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setQuantity(10.0);
        entity.setAveragePurchasePrice(new BigDecimal("100.00"));

        final PortfolioItemSummary summary = calculator.calculate(entity, new BigDecimal("150.00"));

        assertEquals(10.0, summary.totalQuantity());
        assertEquals(new BigDecimal("1000.00"), summary.investedValue());
        assertEquals(new BigDecimal("1500.00"), summary.totalValue());
        assertEquals(new BigDecimal("500.00"), summary.profitValue());
        assertTrue(summary.profitPercentage().contains("50"));
    }

    @Test
    void testCalculateWithNegativeProfit() {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setQuantity(10.0);
        entity.setAveragePurchasePrice(new BigDecimal("150.00"));

        final PortfolioItemSummary summary = calculator.calculate(entity, new BigDecimal("100.00"));

        assertEquals(10.0, summary.totalQuantity());
        assertEquals(new BigDecimal("1500.00"), summary.investedValue());
        assertEquals(new BigDecimal("1000.00"), summary.totalValue());
        assertEquals(new BigDecimal("-500.00"), summary.profitValue());
        assertTrue(summary.profitPercentage().contains("-33"));
    }

    @Test
    void testCalculateWithBreakEven() {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setQuantity(10.0);
        entity.setAveragePurchasePrice(new BigDecimal("100.00"));

        final PortfolioItemSummary summary = calculator.calculate(entity, new BigDecimal("100.00"));

        assertEquals(10.0, summary.totalQuantity());
        assertEquals(new BigDecimal("1000.00"), summary.investedValue());
        assertEquals(new BigDecimal("1000.00"), summary.totalValue());
        assertEquals(new BigDecimal("0.00"), summary.profitValue());
        assertTrue(summary.profitPercentage().contains("0"));
    }

    @Test
    void testCalculateWithNullEntity() {
        final PortfolioItemSummary summary = calculator.calculate(null, new BigDecimal("100.00"));

        assertEquals(0.0, summary.totalQuantity());
        assertEquals(BigDecimal.ZERO, summary.investedValue());
        assertEquals(BigDecimal.ZERO, summary.totalValue());
        assertEquals(BigDecimal.ZERO, summary.profitValue());
        assertEquals("0.00%", summary.profitPercentage());
    }

    @Test
    void testCalculateWithNullPrice() {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setQuantity(10.0);
        entity.setAveragePurchasePrice(new BigDecimal("100.00"));

        final PortfolioItemSummary summary = calculator.calculate(entity, null);

        assertEquals(0.0, summary.totalQuantity());
        assertEquals(BigDecimal.ZERO, summary.investedValue());
        assertEquals(BigDecimal.ZERO, summary.totalValue());
        assertEquals(BigDecimal.ZERO, summary.profitValue());
        assertEquals("0.00%", summary.profitPercentage());
    }

    @Test
    void testCalculateWithNullAveragePurchasePrice() {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setQuantity(10.0);
        entity.setAveragePurchasePrice(null);

        final PortfolioItemSummary summary = calculator.calculate(entity, new BigDecimal("150.00"));

        assertEquals(10.0, summary.totalQuantity());
        assertEquals(new BigDecimal("0.00"), summary.investedValue());
        assertEquals(new BigDecimal("1500.00"), summary.totalValue());
    }

    @Test
    void testCalculateWithZeroQuantity() {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setQuantity(0.0);
        entity.setAveragePurchasePrice(new BigDecimal("100.00"));

        final PortfolioItemSummary summary = calculator.calculate(entity, new BigDecimal("150.00"));

        assertEquals(0.0, summary.totalQuantity());
        assertEquals(new BigDecimal("0.00"), summary.investedValue());
        assertEquals(new BigDecimal("0.00"), summary.totalValue());
    }

    @Test
    void testCalculateWithFractionalQuantity() {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setQuantity(2.5);
        entity.setAveragePurchasePrice(new BigDecimal("100.00"));

        final PortfolioItemSummary summary = calculator.calculate(entity, new BigDecimal("120.00"));

        assertEquals(2.5, summary.totalQuantity());
        assertEquals(new BigDecimal("250.00"), summary.investedValue());
        assertEquals(new BigDecimal("300.00"), summary.totalValue());
        assertEquals(new BigDecimal("50.00"), summary.profitValue());
    }
}
