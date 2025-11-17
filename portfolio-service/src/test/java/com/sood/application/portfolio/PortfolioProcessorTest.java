package com.sood.application.portfolio;

import com.example.market.grpc.PortfolioItem;
import com.example.market.grpc.PortfolioResponse;
import com.sood.application.portfolio.item.ItemProcessor;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioProcessorTest {

    @Mock
    private ItemProcessor itemProcessor;

    @Mock
    private PortfolioResponseBuilder responseBuilder;

    private PortfolioProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new PortfolioProcessor(itemProcessor, responseBuilder);
    }


    @Test
    void testProcessPortfolioWithSingleItem() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol("AAPL");

        final Set<PortfolioItemEntity> items = new HashSet<>();
        items.add(item);
        portfolio.setItems(items);

        final PortfolioItem portfolioItem = PortfolioItem.newBuilder().setSymbol("AAPL").build();
        final PortfolioResponse response = PortfolioResponse.newBuilder().build();

        when(itemProcessor.process(item)).thenReturn(Single.just(portfolioItem));
        when(responseBuilder.build(portfolio, List.of(portfolioItem))).thenReturn(response);

        final Single<PortfolioResponse> result = processor.process(portfolio);

        assertNotNull(result);
        final PortfolioResponse processedResponse = result.blockingGet();
        assertNotNull(processedResponse);
    }

    @Test
    void testProcessPortfolioWithEmptyItems() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setItems(Set.of());

        final PortfolioResponse response = PortfolioResponse.newBuilder().build();

        when(responseBuilder.build(portfolio, new ArrayList<>())).thenReturn(response);

        final Single<PortfolioResponse> result = processor.process(portfolio);

        assertNotNull(result);
        final PortfolioResponse processedResponse = result.blockingGet();
        assertNotNull(processedResponse);
    }

    @Test
    void testProcessPortfolioItemProcessorError() {
        final PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        final PortfolioItemEntity item = new PortfolioItemEntity();
        item.setSymbol("AAPL");

        final Set<PortfolioItemEntity> items = new HashSet<>();
        items.add(item);
        portfolio.setItems(items);

        when(itemProcessor.process(item))
                .thenReturn(Single.error(new RuntimeException("Market data service unavailable")));

        final Single<PortfolioResponse> result = processor.process(portfolio);

        assertNotNull(result);
        assertThrows(RuntimeException.class, result::blockingGet);
    }
}
