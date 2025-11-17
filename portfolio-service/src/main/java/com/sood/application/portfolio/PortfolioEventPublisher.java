package com.sood.application.portfolio;

import com.sood.infrastructure.entity.PortfolioEntity;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.inject.Singleton;

/**
 * Publishes portfolio update events to subscribers.
 * Implements a subject-based event stream for portfolio changes.
 */
@Singleton
public class PortfolioEventPublisher {

    private static final PublishSubject<PortfolioEntity> subject = PublishSubject.create();

    /**
     * Returns an observable stream of portfolio events for a specific portfolio ID.
     *
     * @param portfolioId the portfolio identifier to filter events
     * @return Observable emitting portfolio events for the specified ID
     */
    public static Observable<PortfolioEntity> getObservable(final Long portfolioId) {
        return subject.filter(portfolio -> portfolio.getId().equals(portfolioId));
    }

    /**
     * Emits a portfolio event to all subscribers.
     *
     * @param portfolio the portfolio entity to emit
     */
    public static void emit(PortfolioEntity portfolio) {
        subject.onNext(portfolio);
    }
}
