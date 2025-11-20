package com.sood.market.data.scheduler;

import com.sood.market.data.domain.MarketSession;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class MarketDataRefreshScheduler {

    private final MarketSession session;
    private final SnapshotManager snapshotManager;
    private final MarketDataRefreshService service;

    public MarketDataRefreshScheduler(final MarketSession session, final SnapshotManager snapshotManager,
            final MarketDataRefreshService service) {
        this.session = session;
        this.snapshotManager = snapshotManager;
        this.service = service;
    }

    @Scheduled(fixedDelay = "${scheduler.market-refresh.interval:25m}")
    public void scheduledRefresh() {
        try {
            if (session.isNewDay()) {
                snapshotManager.reset();
            }
            service.refresh();
        } catch (Exception e) {
            log.error("Error in market data refresh scheduler: {}", e.getMessage(), e);
        }
    }
}