package com.sood;

import com.sood.application.portfolio.PortfolioStreamer;
import com.sood.application.portfolio.history.PortfolioHistoryManager;
import com.sood.domain.MarketSessionMonitor;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import io.micronaut.scheduling.annotation.Scheduled;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Singleton;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 * Scheduled task that periodically captures portfolio history snapshots.
 * Executes at the end of each NYSE trading session to store a snapshot of all portfolios.
 */
@Singleton
@Log4j2
public class PortfolioHistoryScheduler {

    private boolean snapshotSaved = false;

    private final PortfolioService service;
    private final PortfolioStreamer streamer;
    private final PortfolioHistoryManager historyManager;
    private final MarketSessionMonitor marketSessionMonitor;

    public PortfolioHistoryScheduler(final PortfolioService service, final PortfolioStreamer streamer,
            final PortfolioHistoryManager historyManager, final MarketSessionMonitor marketSessionMonitor) {
        this.service = service;
        this.streamer = streamer;
        this.historyManager = historyManager;
        this.marketSessionMonitor = marketSessionMonitor;
    }

    /**
     * Scheduled method that runs every 30 minutes.
     * Captures and saves portfolio history snapshots when the NYSE session ends.
     */
    @Scheduled(fixedDelay = "30m")
    public void scheduledRefresh() {
        if (marketSessionMonitor.isEndOfNYSESession() && !snapshotSaved) {
            final List<PortfolioEntity> entities = service.findAll();
            Observable.fromIterable(entities)
                    .flatMapCompletable(portfolio ->
                            streamer.getPortfolioStream(portfolio.getId())
                                    .flatMapCompletable(portfolioResponse ->
                                            Completable.fromAction(() -> historyManager.save(portfolioResponse)))
                                    .doOnError(error -> log.error(
                                            "Error saving portfolio history for portfolioId {}: {}",
                                            portfolio.getId(), error.getMessage(), error))
                    )
                    .doOnComplete(() -> {
                        snapshotSaved = true;
                        log.info("Portfolio history snapshots successfully saved for all portfolios");
                    })
                    .subscribe();
        }
    }
}