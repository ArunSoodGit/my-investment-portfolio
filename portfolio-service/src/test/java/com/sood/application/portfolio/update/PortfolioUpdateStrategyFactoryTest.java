package com.sood.application.portfolio.update;

import com.sood.application.portfolio.update.strategy.BuyStrategy;
import com.sood.application.portfolio.update.strategy.PortfolioUpdateStrategy;
import com.sood.application.portfolio.update.strategy.PortfolioUpdateStrategyFactory;
import com.sood.application.portfolio.update.strategy.SellStrategy;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class PortfolioUpdateStrategyFactoryTest {

    @Mock
    private BuyStrategy buyStrategy;

    @Mock
    private SellStrategy sellStrategy;

    private PortfolioUpdateStrategyFactory factory;

    @BeforeEach
    void setup() {
        factory = new PortfolioUpdateStrategyFactory(buyStrategy, sellStrategy);
    }

    @Test
    void shouldReturnBuyStrategyForBuyTransaction() {
        // given
        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 2.0, BigDecimal.TEN, TransactionType.BUY, LocalDate.now());
        // when
        final PortfolioUpdateStrategy strategy = factory.get(event);

        // then
        assertSame(buyStrategy, strategy);
    }

    @Test
    void shouldReturnSellStrategyForSellTransaction() {
        // given
        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                1L, "AAPL", 2.0, BigDecimal.TEN, TransactionType.SELL, LocalDate.now());

        // when
        final PortfolioUpdateStrategy strategy = factory.get(event);

        // then
        assertSame(sellStrategy, strategy);
    }
}