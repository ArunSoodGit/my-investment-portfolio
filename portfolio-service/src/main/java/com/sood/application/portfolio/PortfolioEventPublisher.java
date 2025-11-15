package com.sood.application.portfolio;

import com.sood.infrastructure.entity.PortfolioEntity;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioEventPublisher {

    private static final PublishSubject<PortfolioEntity> subject = PublishSubject.create();

    public static Observable<PortfolioEntity> getObservable(final Long portfolioId) {
        return subject.filter(portfolio -> portfolio.getId().equals(portfolioId));
    }

    public static void emit(PortfolioEntity portfolio) {
        subject.onNext(portfolio);
    }
}
