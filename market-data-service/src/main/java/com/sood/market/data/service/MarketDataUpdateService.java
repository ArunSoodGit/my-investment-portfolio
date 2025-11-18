package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.sood.market.data.cache.MarketDataCacheManager;
import com.sood.market.data.domain.RateLimiter;
import com.sood.market.data.event.UpdateDataEventPublisher;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

/**
 * Service for updating market data from external API.
 * Coordinates batch updates with rate limiting and event publishing.
 * <p>
 * This replaces MarketDataRefresher and MarketDataUpdater, removing duplication
 * and properly separating concerns.
 */
@Singleton
@Log4j2
public class MarketDataUpdateService {

    private final MarketDataApiClient apiClient;
    private final MarketDataCacheManager cacheManager;
    private final MarketDataPersistenceService persistenceService;
    private final UpdateDataEventPublisher eventPublisher;
    private final RateLimiter rateLimiter;

    public MarketDataUpdateService(
            final MarketDataApiClient apiClient,
            final MarketDataCacheManager cacheManager,
            final MarketDataPersistenceService persistenceService,
            final UpdateDataEventPublisher eventPublisher,
            final RateLimiter rateLimiter) {
        this.apiClient = apiClient;
        this.cacheManager = cacheManager;
        this.persistenceService = persistenceService;
        this.eventPublisher = eventPublisher;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Refreshes market data for all tracked symbols.
     * Fetches all symbols from cache, updates them from API with rate limiting,
     * and publishes update event when complete.
     *
     * @return Completable that completes when all updates are done
     */
    public Completable refreshAllSymbols() {
        log.info("Starting market data refresh for all symbols");

        return cacheManager.getAllSymbols()
                .flatMapCompletable(symbols -> {
                    if (symbols == null || symbols.isEmpty()) {
                        log.warn("No symbols found in cache to refresh");
                        return Completable.complete();
                    }

                    log.info("Refreshing {} symbols from API", symbols.size());
                    return refreshSymbols(symbols);
                })
                .doOnComplete(() -> {
                    log.info("Market data refresh completed, publishing update event");
                    eventPublisher.publish("Market data updated");
                })
                .doOnError(error ->
                        log.error("Error during market data refresh: {}", error.getMessage(), error)
                );
    }

    /**
     * Refreshes market data for a specific set of symbols.
     * Applies rate limiting and handles errors gracefully.
     *
     * @param symbols the set of symbols to refresh
     * @return Completable that completes when all symbols are refreshed
     */
    public Completable refreshSymbols(final Set<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            log.debug("Empty symbol set provided, skipping refresh");
            return Completable.complete();
        }

        log.debug("Refreshing {} symbols with rate limiting", symbols.size());

        return Observable.fromIterable(symbols)
                .compose(rateLimiter::applyRateLimit)
                .flatMapSingle(symbol -> fetchAndCacheSymbol(symbol)
                        .doOnSuccess(data -> log.debug("Successfully updated symbol: {}", symbol))
                        .onErrorResumeNext(error -> {
                            log.error("Failed to update symbol {}: {}", symbol, error.getMessage());
                            return io.reactivex.rxjava3.core.Single.just(createEmptyResponse(symbol));
                        })
                )
                .ignoreElements()
                .doOnComplete(() -> log.info("Completed refresh of {} symbols", symbols.size()));
    }

    /**
     * Fetches market data for a single symbol from API and caches it.
     *
     * @param symbol the stock symbol
     * @return Single emitting the fetched market data
     */
    private Single<MarketDataResponse> fetchAndCacheSymbol(final String symbol) {
        return apiClient.fetchMarketData(symbol)
                .flatMap(marketData ->
                        persistenceService.cacheMarketData(symbol, marketData)
                                .andThen(Single.just(marketData))
                )
                .doOnSuccess(data ->
                        log.trace("Fetched and cached data for {}: price={}", symbol, data.getPrice())
                );
    }

    /**
     * Creates an empty response for error cases.
     * Used to continue processing other symbols when one fails.
     *
     * @param symbol the symbol that failed
     * @return empty MarketDataResponse
     */
    private MarketDataResponse createEmptyResponse(final String symbol) {
        return MarketDataResponse.newBuilder()
                .setSymbol(symbol)
                .build();
    }
}
