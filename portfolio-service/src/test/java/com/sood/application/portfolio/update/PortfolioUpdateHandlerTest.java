package com.sood.application.portfolio.update;

import com.sood.application.portfolio.PortfolioEventPublisher;
import com.sood.application.portfolio.update.strategy.PortfolioUpdateStrategy;
import com.sood.application.portfolio.update.strategy.PortfolioUpdateStrategyFactory;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioUpdateHandlerTest {

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private PortfolioUpdateStrategyFactory strategyFactory;

    @Mock
    private PortfolioEventPublisher eventPublisher;

    @Mock
    private PortfolioPersistenceService portfolioPersistenceService;

    @Mock
    private PortfolioUpdateStrategy strategy;

    private PortfolioUpdateHandler handler;
    private final LocalDate now = LocalDate.now();

    @BeforeEach
    void setUp() {
        handler = new PortfolioUpdateHandler(portfolioService, strategyFactory, eventPublisher, portfolioPersistenceService);
    }

    @Test
    void testHandleTransactionWithNewItem() {
        final long portfolioId = 1L;
        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                portfolioId, "AAPL", 10.0,
                new BigDecimal("150.00"), TransactionType.BUY, now
        );

        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(portfolioId);

        when(portfolioService.getPortfolio(portfolioId)).thenReturn(portfolio);
        when(strategyFactory.get(event)).thenReturn(strategy);

        handler.handle(event);

        verify(portfolioService).getPortfolio(portfolioId);
        verify(strategyFactory).get(event);
        verify(strategy).update(portfolio, event);
    }

    @Test
    void testHandleTransactionWithExistingItem() {
        final long portfolioId = 2L;
        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                portfolioId, "GOOGL", 5.0,
                new BigDecimal("2800.00"), TransactionType.BUY, now
        );

        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(portfolioId);

        when(portfolioService.getPortfolio(portfolioId)).thenReturn(portfolio);
        when(strategyFactory.get(event)).thenReturn(strategy);

        handler.handle(event);

        verify(portfolioService).getPortfolio(portfolioId);
        verify(strategyFactory).get(event);
        verify(strategy).update(portfolio, event);
    }

    @Test
    void testHandleSellTransaction() {
        final long portfolioId = 1L;
        final TransactionCreatedEvent event = new TransactionCreatedEvent(
                portfolioId, "AAPL", 5.0,
                new BigDecimal("160.00"), TransactionType.SELL, now
        );

        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(portfolioId);

        when(portfolioService.getPortfolio(portfolioId)).thenReturn(portfolio);
        when(strategyFactory.get(event)).thenReturn(strategy);

        handler.handle(event);

        verify(portfolioService).getPortfolio(portfolioId);
        verify(strategyFactory).get(event);
        verify(strategy).update(portfolio, event);
    }

    @Test
    void testHandleMultipleTransactions() {
        final long portfolioId = 1L;

        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(portfolioId);

        final TransactionCreatedEvent event1 = new TransactionCreatedEvent(
                portfolioId, "AAPL", 10.0,
                new BigDecimal("150.00"), TransactionType.BUY, now
        );

        final TransactionCreatedEvent event2 = new TransactionCreatedEvent(
                portfolioId, "AAPL", 5.0,
                new BigDecimal("155.00"), TransactionType.BUY, now
        );

        when(portfolioService.getPortfolio(portfolioId)).thenReturn(portfolio);
        when(strategyFactory.get(event1)).thenReturn(strategy);
        when(strategyFactory.get(event2)).thenReturn(strategy);

        handler.handle(event1);
        handler.handle(event2);

        verify(portfolioService, times(2)).getPortfolio(portfolioId);
        verify(strategyFactory, times(2)).get(any(TransactionCreatedEvent.class));
        verify(strategy, times(2)).update(eq(portfolio), any(TransactionCreatedEvent.class));
    }
}
