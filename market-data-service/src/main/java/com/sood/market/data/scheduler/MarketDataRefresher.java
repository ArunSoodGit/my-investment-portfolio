package com.sood.market.data.scheduler;

import io.reactivex.rxjava3.core.Completable;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class MarketDataRefresher {

    private final MarketDataUpdater marketDataUpdater;
    private final MarketDataCacheManager cacheManager;

    public MarketDataRefresher(final MarketDataUpdater marketDataUpdater, final MarketDataCacheManager cacheManager) {
        this.marketDataUpdater = marketDataUpdater;
        this.cacheManager = cacheManager;
    }

    /**
     * Odświeża wszystkie symbole zależnie od stanu rynku.
     * - Jeśli NYSE otwarte → aktualizujemy dane z API
     * - Jeśli zamknięte → zapisujemy do bazy ostatnie dane z cache
     */
    public void refreshAllSymbols() {
        log.info("Rozpoczynam odświeżanie danych rynkowych...");

        cacheManager.getAllSymbols()
                .flatMapCompletable(symbols -> {
                    if (symbols == null || symbols.isEmpty()) {
                        log.warn("Brak symboli w cache do odświeżenia.");
                        return Completable.complete();
                    }

                    return marketDataUpdater.updateCacheFromApi(symbols)
                            .doOnComplete(() -> log.info("Zakończono aktualizację cache z API."));

                })
                .subscribe(
                        () -> log.info("Odświeżanie zakończone pomyślnie."),
                        error -> log.error("Błąd podczas odświeżania danych: {}", error.getMessage(), error)
                );
    }
}