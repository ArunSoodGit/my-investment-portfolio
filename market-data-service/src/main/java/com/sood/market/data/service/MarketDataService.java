package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.sood.market.data.cache.MarketDataCacheManager;
import com.sood.market.data.exception.MarketDataNotFoundException;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

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

    public Single<MarketDataResponse> getMarketData(final String symbol) {
        log.debug("Retrieving market data for symbol: {}", symbol);

        return cache.get(symbol)
                .switchIfEmpty(fetchAndCacheFromApi(symbol))
                .doOnError(error -> log.error("Failed to retrieve market data for symbol: {}",
                        symbol, error));
    }

    public void refreshMarketDataInDatabase() {
        log.info("Starting market data database refresh");
        persistenceService.refreshDatabaseFromCache();
        log.info("Market data database refresh completed");
    }

    private Single<MarketDataResponse> fetchAndCacheFromApi(final String symbol) {
        return apiClient.fetchMarketData(symbol)
                .flatMap(marketData -> persistenceService.cacheMarketData(symbol, marketData)
                        .andThen(Single.just(marketData)))
                .onErrorResumeNext(error -> handleApiFetchError(symbol, error));
    }

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
