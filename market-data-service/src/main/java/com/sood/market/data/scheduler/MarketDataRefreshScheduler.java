package com.sood.market.data.scheduler;

import com.sood.market.data.domain.MarketSession;
import com.sood.market.data.service.MarketDataService;
import com.sood.market.data.service.MarketDataUpdateService;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

/**
 * Scheduler for periodic market data refresh operations.
 * Coordinates updates based on market session state.
 * <p>
 * Schedule:
 * - During market hours: Refresh data from API
 * - After market close: Save snapshot to database (once per day)
 * - Outside market hours: No action
 */
@Singleton
@Log4j2
public class MarketDataRefreshScheduler {

    private final MarketDataUpdateService updateService;
    private final MarketDataService dataService;
    private final MarketSession marketSession;

    private boolean snapshotSavedToday = false;

    public MarketDataRefreshScheduler(final MarketDataUpdateService updateService, final MarketDataService dataService,
            final MarketSession marketSession) {
        this.updateService = updateService;
        this.dataService = dataService;
        this.marketSession = marketSession;
    }

    /**
     * Main scheduler method that runs periodically.
     * Determines action based on current market state.
     */
    @Scheduled(fixedDelay = "${scheduler.market-refresh.interval:25m}")
    public void scheduledRefresh() {
        try {
            if (marketSession.isNewDay()) {
                resetDailyFlags();
            }

            if (marketSession.isMarketOpen()) {
                refreshDuringMarketHours();
            } else if (marketSession.isAfterMarketClose() && !snapshotSavedToday) {
                saveEndOfDaySnapshot();
            } else {
                log.debug("Outside market hours and snapshot already saved - no action");
            }

        } catch (Exception e) {
            log.error("Error in market data refresh scheduler: {}", e.getMessage(), e);
        }
    }

    /**
     * Refreshes market data during active trading hours.
     */
    private void refreshDuringMarketHours() {
        log.info("Market is open - refreshing data from API");

        updateService.refreshAllSymbols()
                .subscribe(
                        () -> {
                            log.info("Market data refresh completed successfully");
                            snapshotSavedToday = false; // Reset flag for next day
                        },
                        error -> log.error("Failed to refresh market data: {}", error.getMessage(), error)
                );
    }

    /**
     * Saves end-of-day snapshot to database after market close.
     */
    private void saveEndOfDaySnapshot() {
        log.info("Market closed - saving end-of-day snapshot to database");

        try {
            dataService.refreshMarketDataInDatabase();
            snapshotSavedToday = true;
            log.info("End-of-day snapshot saved successfully");
        } catch (Exception e) {
            log.error("Failed to save end-of-day snapshot: {}", e.getMessage(), e);
        }
    }

    /**
     * Resets daily flags at the start of a new day.
     */
    private void resetDailyFlags() {
        if (snapshotSavedToday) {
            log.debug("New day detected - resetting snapshot flag");
            snapshotSavedToday = false;
        }
    }

}
