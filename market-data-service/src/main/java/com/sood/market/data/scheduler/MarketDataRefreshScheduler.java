package com.sood.market.data.scheduler;

import com.sood.market.data.service.MarketDataService;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

import static com.sood.market.data.scheduler.MarketSessionMonitor.isEndOfNYSESession;
import static com.sood.market.data.scheduler.MarketSessionMonitor.isNYSEOpen;

@Singleton
@Log4j2
public class MarketDataRefreshScheduler {

    private boolean snapshotSaved = false; // flaga, by nie zapisywać danych wiele razy po zamknięciu sesji

    private final MarketDataRefresher refresher;
    private final MarketDataService service;

    public MarketDataRefreshScheduler(final MarketDataRefresher refresher, final MarketDataService service) {
        this.refresher = refresher;
        this.service = service;
    }

    /**
     * Główny scheduler uruchamiany co 15 minut.
     */
    @Scheduled(fixedDelay = "25m")
    public void scheduledRefresh() {
        try {
            if (isNYSEOpen()) {
                log.info("📈 NYSE otwarte — odświeżam dane z API.");
                refresher.refreshAllSymbols();
                snapshotSaved = false; // reset flagi
            } else if (isEndOfNYSESession() && !snapshotSaved) {
                log.info("📉 NYSE zamknięte — zapisuję ostatnie dane z cache do bazy.");
                service.refreshMarketData(); // zapis ostatnich wartości
                snapshotSaved = true;

            } else {
                log.info("💤 Poza godzinami sesji — brak akcji.");
            }
        } catch (Exception e) {
            log.error("Błąd w schedulerze odświeżania danych: {}", e.getMessage(), e);
        }
    }
}