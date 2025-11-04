package com.sood;

import com.sood.application.portfolio.PortfolioStreamer;
import com.sood.application.portfolio.history.PortfolioHistoryManager;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import io.micronaut.scheduling.annotation.Scheduled;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Singleton;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class PortfolioHistoryScheduler {

    private boolean snapshotSaved = false;

    private final PortfolioService service;
    private final PortfolioStreamer streamer;
    private final PortfolioHistoryManager historyManager;

    public PortfolioHistoryScheduler(final PortfolioService service, final PortfolioStreamer streamer,
            final PortfolioHistoryManager historyManager) {
        this.service = service;
        this.streamer = streamer;
        this.historyManager = historyManager;
    }

    @Scheduled(fixedDelay = "30m")
    public void scheduledRefresh() {
        if (MarketSessionMonitor.isEndOfNYSESession() && !snapshotSaved) {
            // Pobieramy wszystkie portfele z serwisu
            final List<PortfolioEntity> entities = service.findAll();
            Observable.fromIterable(entities) // tworzymy strumień portfeli
                    .flatMapCompletable(portfolio ->
                            streamer.getPortfolioStream(portfolio.getId()) // pobieramy strumień dla danego portfela
                                    .flatMapCompletable(p -> Completable.fromAction(() -> historyManager.save(p)))
                                    .doOnError(error -> log.error(
                                            "Błąd przy zapisie historii portfela {}: {}",
                                            portfolio.getId(), error.getMessage(), error))
                    )
                    .doOnComplete(() -> {
                        snapshotSaved = true;
                        log.info("Zapisano historię wszystkich portfeli.");
                    })
                    .subscribe();
        }
    }
}