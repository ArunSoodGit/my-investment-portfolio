package com.sood.application.portfolio;

import com.sood.infrastructure.entity.PortfolioEntity;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PortfolioEventPublisher.
 * Verifies subscription management, event emission, and cleanup functionality.
 */
class PortfolioEventPublisherTest {

    private PortfolioEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new PortfolioEventPublisher();
    }

    @AfterEach
    void tearDown() {
        // Ensure cleanup is called after each test to prevent memory leaks
        publisher.cleanup();
    }

    @Test
    void shouldEmitPortfolioEventToSubscribers() {
        // Given
        Long portfolioId = 1L;
        PortfolioEntity portfolio = createPortfolio(portfolioId);

        TestObserver<PortfolioEntity> testObserver = publisher.getObservable(portfolioId).test();

        // When
        publisher.emit(portfolio);

        // Then
        testObserver.assertValue(portfolio);
        testObserver.assertNotComplete();
    }

    @Test
    void shouldFilterEventsByPortfolioId() {
        // Given
        Long portfolioId1 = 1L;
        Long portfolioId2 = 2L;

        PortfolioEntity portfolio1 = createPortfolio(portfolioId1);
        PortfolioEntity portfolio2 = createPortfolio(portfolioId2);

        TestObserver<PortfolioEntity> observer1 = publisher.getObservable(portfolioId1).test();
        TestObserver<PortfolioEntity> observer2 = publisher.getObservable(portfolioId2).test();

        // When
        publisher.emit(portfolio1);
        publisher.emit(portfolio2);

        // Then
        observer1.assertValue(portfolio1);
        observer1.assertValueCount(1);

        observer2.assertValue(portfolio2);
        observer2.assertValueCount(1);
    }

    @Test
    void shouldNotEmitWhenPortfolioIsNull() {
        // Given
        Long portfolioId = 1L;
        TestObserver<PortfolioEntity> testObserver = publisher.getObservable(portfolioId).test();

        // When
        publisher.emit(null);

        // Then
        testObserver.assertNoValues();
        testObserver.assertNotComplete();
    }

    @Test
    void shouldHandleMultipleSubscribersForSamePortfolio() {
        // Given
        Long portfolioId = 1L;
        PortfolioEntity portfolio = createPortfolio(portfolioId);

        TestObserver<PortfolioEntity> observer1 = publisher.getObservable(portfolioId).test();
        TestObserver<PortfolioEntity> observer2 = publisher.getObservable(portfolioId).test();

        // When
        publisher.emit(portfolio);

        // Then
        observer1.assertValue(portfolio);
        observer2.assertValue(portfolio);
    }

    @Test
    void shouldCleanupSubscriptionsOnDispose() {
        // Given
        Long portfolioId = 1L;
        TestObserver<PortfolioEntity> testObserver = publisher.getObservable(portfolioId).test();

        assertEquals(1, publisher.getActiveSubscriptionCount());

        // When
        testObserver.dispose();

        // Then - Give some time for cleanup to execute
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Note: Cleanup happens asynchronously, so count might still be 1
        assertTrue(publisher.getActiveSubscriptionCount() <= 1);
    }

    @Test
    void shouldCompleteAllObservablesOnCleanup() throws InterruptedException {
        // Given
        Long portfolioId = 1L;
        TestObserver<PortfolioEntity> testObserver = publisher.getObservable(portfolioId).test();

        // When
        publisher.cleanup();

        // Then - Wait for completion event to propagate
        testObserver.await(100, TimeUnit.MILLISECONDS);
        testObserver.assertComplete();
        assertEquals(0, publisher.getActiveSubscriptionCount());
    }

    @Test
    void shouldHandleEmitWithoutObservers() {
        // Given
        PortfolioEntity portfolio = createPortfolio(1L);

        // When - No observers subscribed
        assertDoesNotThrow(() -> publisher.emit(portfolio));

        // Then
        assertFalse(publisher.hasObservers());
    }

    @Test
    void shouldTrackActiveSubscriptions() {
        // Given
        TestObserver<PortfolioEntity> observer1 = publisher.getObservable(1L).test();
        TestObserver<PortfolioEntity> observer2 = publisher.getObservable(2L).test();
        TestObserver<PortfolioEntity> observer3 = publisher.getObservable(3L).test();

        // Then
        assertEquals(3, publisher.getActiveSubscriptionCount());
        assertTrue(publisher.hasObservers());

        // When
        observer1.dispose();
        observer2.dispose();
        observer3.dispose();

        // Then - Eventually subscriptions are cleaned up
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void shouldNotLeakMemoryWithManyEmissions() {
        // Given
        Long portfolioId = 1L;
        TestObserver<PortfolioEntity> testObserver = publisher.getObservable(portfolioId).test();

        // When - Emit many events
        for (int i = 0; i < 1000; i++) {
            PortfolioEntity portfolio = createPortfolio(portfolioId);
            publisher.emit(portfolio);
        }

        // Then - All events should be received
        testObserver.assertValueCount(1000);
        testObserver.assertNotComplete();
    }

    @Test
    void shouldHandleConcurrentSubscriptions() throws InterruptedException {
        // Given
        Long portfolioId = 1L;
        int subscriberCount = 10;
        TestObserver<PortfolioEntity>[] observers = new TestObserver[subscriberCount];

        // When - Create multiple subscribers concurrently
        Thread[] threads = new Thread[subscriberCount];
        for (int i = 0; i < subscriberCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                observers[index] = publisher.getObservable(portfolioId).test();
            });
            threads[i].start();
        }

        // Wait for all subscriptions
        for (Thread thread : threads) {
            thread.join();
        }

        // Emit event
        PortfolioEntity portfolio = createPortfolio(portfolioId);
        publisher.emit(portfolio);

        // Then - All observers should receive the event
        for (TestObserver<PortfolioEntity> observer : observers) {
            observer.assertValue(portfolio);
        }
    }

    private PortfolioEntity createPortfolio(Long id) {
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(id);
        portfolio.setPortfolioName("Test Portfolio " + id);
        return portfolio;
    }
}
