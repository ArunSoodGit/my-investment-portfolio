package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.sood.market.data.cache.MarketDataCacheManager;
import com.sood.market.data.exception.MarketDataNotFoundException;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

/**
 * Main service for retrieving market data.
 * Follows Single Responsibility Principle and Dependency Inversion Principle.
 * Coordinates between cache, API, and persistence layers.
 */
@Singleton
@Log4j2
public class MarketDataService {

    private final MarketDataCacheManager cache;
    private final MarketDataApiClient apiClient;
    private final MarketDataPersistenceService persistenceService;

    public MarketDataService(final MarketDataCacheManager cache, final MarketDataApiClient apiClient,
            final MarketDataPersistenceService persistenceService) {
        this.cache = cache;
        this.apiClient = apiClient;
        this.persistenceService = persistenceService;
    }

    /**
     * Retrieves market data for a given symbol.
     * Uses cache-aside pattern: tries cache first, then fetches from API if not found.
     *
     * @param symbol the stock symbol (e.g., "AAPL", "GOOGL")
     * @return Single emitting MarketDataResponse
     */
    public Single<MarketDataResponse> getMarketData(final String symbol) {
        log.debug("Retrieving market data for symbol: {}", symbol);

        return cache.get(symbol)
                .switchIfEmpty(fetchAndCacheFromApi(symbol))
                .doOnSuccess(data -> log.debug("Retrieved market data for {}: price={}",
                        symbol, data.getPrice()))
                .doOnError(error -> log.error("Failed to retrieve market data for symbol: {}",
                        symbol, error));
    }

    /**
     * Refreshes all market data in the database from cache.
     * This is typically called by a scheduler to persist cached data.
     */
    public void refreshMarketDataInDatabase() {
        log.info("Starting market data database refresh");
        persistenceService.refreshDatabaseFromCache();
        log.info("Market data database refresh completed");
    }

    /**
     * Fetches market data from API and caches the result.
     *
     * @param symbol the stock symbol
     * @return Single emitting MarketDataResponse
     */
    private Single<MarketDataResponse> fetchAndCacheFromApi(final String symbol) {
        return apiClient.fetchMarketData(symbol)
                .flatMap(marketData -> persistenceService.cacheMarketData(symbol, marketData)
                        .andThen(Single.just(marketData)))
                .onErrorResumeNext(error -> handleApiFetchError(symbol, error));
    }

    /**
     * Handles errors when fetching from API.
     * Attempts to fall back to cached data if available.
     *
     * @param symbol the stock symbol
     * @param error  the error that occurred
     * @return Single with fallback data or error
     */
    private Single<MarketDataResponse> handleApiFetchError(final String symbol, final Throwable error) {
        log.warn("API fetch failed for symbol {}, attempting cache fallback: {}",
                symbol, error.getMessage());

        return cache.get(symbol)
                .switchIfEmpty(Single.error(
                        new MarketDataNotFoundException(
                                "Market data not available in cache or API for symbol: " + symbol,
                                error)));
    }
}
