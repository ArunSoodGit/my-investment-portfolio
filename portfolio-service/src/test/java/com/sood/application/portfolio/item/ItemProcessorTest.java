package com.sood.application.portfolio.item;

import com.example.market.grpc.MarketDataResponse;
import com.example.market.grpc.PortfolioItem;
import com.sood.client.MarketDataServiceClient;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import io.reactivex.rxjava3.core.Single;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemProcessorTest {

    @Mock
    private MarketDataServiceClient client;

    @Mock
    private PortfolioItemBuilder itemBuilder;

    private ItemProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ItemProcessor(client, itemBuilder);
    }

    @Test
    void testProcessItemWithValidMarketData() {
        final PortfolioItemEntity itemEntity = new PortfolioItemEntity();
        itemEntity.setSymbol("AAPL");
        itemEntity.setQuantity(10.0);
        itemEntity.setAveragePurchasePrice(new BigDecimal("150.00"));

        final MarketDataResponse marketData = MarketDataResponse.newBuilder()
                .setSymbol("AAPL")
                .setPrice("160.00")
                .setCompanyName("Apple Inc.")
                .setExchange("NASDAQ")
                .setPercentageChange("2.5")
                .build();
        final PortfolioItem expectedItem = PortfolioItem.newBuilder().setSymbol("AAPL").setQuantity(10).build();

        when(client.getMarketData("AAPL")).thenReturn(Single.just(marketData));
        when(itemBuilder.build(itemEntity, marketData)).thenReturn(expectedItem);

        final Single<PortfolioItem> result = processor.process(itemEntity);

        assertNotNull(result);
        final PortfolioItem item = result.blockingGet();
        assertEquals("AAPL", item.getSymbol());
    }

    @Test
    void testProcessItemWithDifferentSymbols() {
        final PortfolioItemEntity itemEntity = new PortfolioItemEntity();
        itemEntity.setSymbol("GOOGL");
        itemEntity.setQuantity(5.0);
        itemEntity.setAveragePurchasePrice(new BigDecimal("2800.00"));

        final MarketDataResponse marketData = MarketDataResponse.newBuilder()
                .setSymbol("GOOGL")
                .setPrice("2850.00")
                .setCompanyName("Alphabet Inc.")
                .setExchange("NASDAQ")
                .setPercentageChange("1.8")
                .build();
        final PortfolioItem expectedItem = PortfolioItem.newBuilder().setSymbol("GOOGL").setQuantity(5).build();

        when(client.getMarketData("GOOGL")).thenReturn(Single.just(marketData));
        when(itemBuilder.build(itemEntity, marketData)).thenReturn(expectedItem);

        final Single<PortfolioItem> result = processor.process(itemEntity);

        assertNotNull(result);
        final PortfolioItem item = result.blockingGet();
        assertEquals("GOOGL", item.getSymbol());
    }

    @Test
    void testProcessItemMarketDataNotAvailable() {
        final PortfolioItemEntity itemEntity = new PortfolioItemEntity();
        itemEntity.setSymbol("AAPL");

        when(client.getMarketData("AAPL"))
                .thenReturn(Single.error(new RuntimeException("Market data service unavailable")));

        final Single<PortfolioItem> result = processor.process(itemEntity);

        assertNotNull(result);
        assertThrows(RuntimeException.class, result::blockingGet);
    }

    @Test
    void testProcessMultipleItems() {
        final PortfolioItemEntity item1 = new PortfolioItemEntity();
        item1.setSymbol("AAPL");

        final PortfolioItemEntity item2 = new PortfolioItemEntity();
        item2.setSymbol("MSFT");

        final MarketDataResponse marketData1 = MarketDataResponse.newBuilder()
                .setSymbol("AAPL")
                .setPrice("160.00")
                .setCompanyName("Apple Inc.")
                .setExchange("NASDAQ")
                .setPercentageChange("2.5")
                .build();
        final MarketDataResponse marketData2 = MarketDataResponse.newBuilder()
                .setSymbol("MSFT")
                .setPrice("380.00")
                .setCompanyName("Microsoft Corporation")
                .setExchange("NASDAQ")
                .setPercentageChange("1.2")
                .build();

        final PortfolioItem expectedItem1 = PortfolioItem.newBuilder().setSymbol("AAPL").build();
        final PortfolioItem expectedItem2 = PortfolioItem.newBuilder().setSymbol("MSFT").build();

        when(client.getMarketData("AAPL")).thenReturn(Single.just(marketData1));
        when(client.getMarketData("MSFT")).thenReturn(Single.just(marketData2));
        when(itemBuilder.build(item1, marketData1)).thenReturn(expectedItem1);
        when(itemBuilder.build(item2, marketData2)).thenReturn(expectedItem2);

        final Single<PortfolioItem> result1 = processor.process(item1);
        final Single<PortfolioItem> result2 = processor.process(item2);

        final PortfolioItem processedItem1 = result1.blockingGet();
        final PortfolioItem processedItem2 = result2.blockingGet();

        assertEquals("AAPL", processedItem1.getSymbol());
        assertEquals("MSFT", processedItem2.getSymbol());
    }
}
