package com.sood.application.portfolio;

import com.sood.infrastructure.entity.PortfolioEntity;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Publishes portfolio update events to subscribers.
 * Implements a subject-based event stream for portfolio changes.
 */
@Singleton
public class PortfolioEventPublisher {

    private static final Logger log = LogManager.getLogger(PortfolioEventPublisher.class);

    // Non-static subject instance managed by the singleton bean
    private final PublishSubject<PortfolioEntity> subject;

    // Track active subscriptions for cleanup
    private final Map<Long, CompositeDisposable> activeSubscriptions;


    public PortfolioEventPublisher() {
        this.subject = PublishSubject.create();
        this.activeSubscriptions = new ConcurrentHashMap<>();

        log.info("PortfolioEventPublisher initialized");
    }

    /**
     * Returns an observable stream of portfolio events for a specific portfolio ID.
     * The returned observable will automatically unsubscribe on error or completion.
     *
     * @param portfolioId the portfolio identifier to filter events
     * @return Observable emitting portfolio events for the specified ID
     */
    public Observable<PortfolioEntity> getObservable(final Long portfolioId) {
        return subject
                .filter(portfolio -> portfolio.getId().equals(portfolioId))
                .doOnSubscribe(disposable -> {
                    log.debug("New subscription for portfolio {}", portfolioId);
                    activeSubscriptions
                            .computeIfAbsent(portfolioId, k -> new CompositeDisposable())
                            .add(disposable);
                })
                .doOnDispose(() -> {
                    log.debug("Subscription disposed for portfolio {}", portfolioId);
                    cleanupSubscriptions(portfolioId);
                })
                .doOnComplete(() -> log.debug("Observable completed for portfolio {}", portfolioId))
                .share(); // Share the subscription among multiple observers
    }

    /**
     * Emits a portfolio event to all subscribers.
     *
     * @param portfolio the portfolio entity to emit
     */
    public void emit(final PortfolioEntity portfolio) {
        if (portfolio == null) {
            log.warn("Attempted to emit null portfolio");
            return;
        }

        if (subject.hasObservers()) {
            subject.onNext(portfolio);
            log.debug("Emitted portfolio event for ID: {}", portfolio.getId());
        } else {
            log.debug("No active observers for portfolio event ID: {}", portfolio.getId());
        }
    }

    /**
     * Cleanup subscriptions for a specific portfolio ID when no longer needed.
     */
    private void cleanupSubscriptions(final Long portfolioId) {
        final CompositeDisposable disposables = activeSubscriptions.get(portfolioId);
        if (disposables != null && disposables.size() == 0) {
            activeSubscriptions.remove(portfolioId);
            log.debug("Cleaned up subscriptions for portfolio {}", portfolioId);
        }
    }
}
