package com.sood.api;

import com.example.market.grpc.PortfolioHistoryItem;
import com.example.market.grpc.PortfolioHistoryResponse;
import com.example.market.grpc.PortfolioRequest;
import com.sood.application.portfolio.history.PortfolioHistoryManager;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioHistoryControllerTest {

    @Mock
    private PortfolioHistoryManager historyManager;

    @Mock
    private StreamObserver<PortfolioHistoryResponse> responseObserver;

    private PortfolioHistoryController controller;

    @BeforeEach
    void setUp() {
        controller = new PortfolioHistoryController(historyManager);
    }

    @Test
    void testGetPortfolioHistorySuccess() {
        final long portfolioId = 1L;
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        final PortfolioHistoryResponse expectedResponse = PortfolioHistoryResponse.newBuilder()
                .addItems(PortfolioHistoryItem.newBuilder()
                        .setDate("2024-01-01")
                        .setCurrentValue("1000")
                        .setInvestedValue("200")
                        .build())
                .build();

        when(historyManager.get(portfolioId)).thenReturn(expectedResponse);

        controller.getPortfolioHistory(request, responseObserver);

        ArgumentCaptor<PortfolioHistoryResponse> responseCaptor = ArgumentCaptor.forClass(PortfolioHistoryResponse.class);
        verify(historyManager).get(portfolioId);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        PortfolioHistoryResponse capturedResponse = responseCaptor.getValue();
        assertNotNull(capturedResponse);
        assertEquals(1, capturedResponse.getItemsCount());
        
        PortfolioHistoryItem item = capturedResponse.getItems(0);
        assertEquals("2024-01-01", item.getDate());
        assertEquals("1000", item.getCurrentValue());
        assertEquals("200", item.getInvestedValue());
    }

    @Test
    void testGetPortfolioHistoryWithEmptyHistory() {
        final long portfolioId = 2L;
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        final PortfolioHistoryResponse expectedResponse = PortfolioHistoryResponse.newBuilder()
                .build();

        when(historyManager.get(portfolioId)).thenReturn(expectedResponse);

        controller.getPortfolioHistory(request, responseObserver);

        ArgumentCaptor<PortfolioHistoryResponse> responseCaptor = ArgumentCaptor.forClass(PortfolioHistoryResponse.class);
        verify(historyManager).get(portfolioId);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        PortfolioHistoryResponse capturedResponse = responseCaptor.getValue();
        assertNotNull(capturedResponse);
        assertEquals(0, capturedResponse.getItemsCount());
    }

    @Test
    void testGetPortfolioHistoryThrowsException() {
        final long portfolioId = 1L;
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        final RuntimeException exception = new RuntimeException("Portfolio not found");
        when(historyManager.get(portfolioId)).thenThrow(exception);

        controller.getPortfolioHistory(request, responseObserver);

        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
        verify(historyManager).get(portfolioId);
        verify(responseObserver).onError(exceptionCaptor.capture());

        Exception caughtException = exceptionCaptor.getValue();
        assertNotNull(caughtException);
        assertEquals(exception, caughtException);
    }

    @Test
    void testGetPortfolioHistoryWithNullPointerException() {
        final long portfolioId = 3L;
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        final NullPointerException exception = new NullPointerException("Portfolio data is null");
        when(historyManager.get(portfolioId)).thenThrow(exception);

        controller.getPortfolioHistory(request, responseObserver);

        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
        verify(responseObserver).onError(exceptionCaptor.capture());

        Exception caughtException = exceptionCaptor.getValue();
        assertInstanceOf(NullPointerException.class, caughtException);
        assertEquals("Portfolio data is null", caughtException.getMessage());
    }

    @Test
    void testGetPortfolioHistoryWithIllegalArgumentException() {
        final long portfolioId = -1L;
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        final IllegalArgumentException exception = new IllegalArgumentException("Invalid portfolio ID");
        when(historyManager.get(portfolioId)).thenThrow(exception);

        controller.getPortfolioHistory(request, responseObserver);

        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
        verify(responseObserver).onError(exceptionCaptor.capture());

        Exception caughtException = exceptionCaptor.getValue();
        assertInstanceOf(IllegalArgumentException.class, caughtException);
    }

    @Test
    void testGetPortfolioHistoryCompletedSuccessfully() {
        final long portfolioId = 100L;
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        final PortfolioHistoryResponse response = PortfolioHistoryResponse.newBuilder()
                .addItems(PortfolioHistoryItem.newBuilder()
                        .setDate("2024-01-15")
                        .setCurrentValue("1500")
                        .setInvestedValue("200")
                        .build())
                .build();

        when(historyManager.get(portfolioId)).thenReturn(response);

        controller.getPortfolioHistory(request, responseObserver);

        ArgumentCaptor<PortfolioHistoryResponse> responseCaptor = ArgumentCaptor.forClass(PortfolioHistoryResponse.class);
        verify(historyManager).get(portfolioId);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        PortfolioHistoryResponse capturedResponse = responseCaptor.getValue();
        assertNotNull(capturedResponse);
        assertEquals(1, capturedResponse.getItemsCount());
    }

    @Test
    void testGetPortfolioHistoryWithDifferentPortfolioIds() {
        final long portfolioId1 = 1L;
        final long portfolioId2 = 2L;

        final PortfolioRequest request1 = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId1)
                .build();

        final PortfolioRequest request2 = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId2)
                .build();

        final PortfolioHistoryResponse response1 = PortfolioHistoryResponse.newBuilder()
                .addItems(PortfolioHistoryItem.newBuilder()
                        .setDate("2024-01-01")
                        .setCurrentValue("1000")
                        .setInvestedValue("200")
                        .build())
                .build();

        final PortfolioHistoryResponse response2 = PortfolioHistoryResponse.newBuilder()
                .addItems(PortfolioHistoryItem.newBuilder()
                        .setDate("2024-01-02")
                        .setCurrentValue("2000")
                        .setInvestedValue("300")
                        .build())
                .build();

        when(historyManager.get(portfolioId1)).thenReturn(response1);
        when(historyManager.get(portfolioId2)).thenReturn(response2);

        controller.getPortfolioHistory(request1, responseObserver);
        controller.getPortfolioHistory(request2, responseObserver);

        verify(historyManager).get(portfolioId1);
        verify(historyManager).get(portfolioId2);
        verify(responseObserver).onNext(response1);
        verify(responseObserver).onNext(response2);
    }
}
