package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.sood.market.data.infrastructure.MarketDataRepository;
import com.sood.market.data.infrastructure.entity.MarketDataEntity;
import com.sood.market.data.cache.MarketDataCacheManager;
import io.reactivex.rxjava3.core.Completable;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;
import java.util.Set;

/**
 * Handles persistence operations for market data.
 * Follows Single Responsibility Principle - only manages data persistence.
 */
@Singleton
@Log4j2
public class MarketDataPersistenceService {

    private final MarketDataRepository repository;
    private final MarketDataCacheManager cache;
    private final MarketDataMapper mapper;

    public MarketDataPersistenceService(
            final MarketDataRepository repository,
            final MarketDataCacheManager cache,
            final MarketDataMapper mapper) {
        this.repository = repository;
        this.cache = cache;
        this.mapper = mapper;
    }

    /**
     * Persists market data to database.
     *
     * @param marketData the market data to persist
     */
    public void persistMarketData(final MarketDataResponse marketData) {
        final MarketDataEntity entity = mapper.toEntity(marketData);
        repository.save(entity);
        log.debug("Persisted market data for symbol: {}", marketData.getSymbol());
    }

    /**
     * Refreshes all market data in database from cache.
     * WARNING: This deletes all existing data before refresh.
     */
    public void refreshDatabaseFromCache() {
        log.info("Starting database refresh from cache");

        // Clear existing data
        repository.deleteAll();
        log.debug("Cleared existing market data from database");

        // Fetch all symbols from cache
        final Set<String> symbols = cache.getAllSymbols().blockingGet();
        log.info("Found {} symbols in cache to refresh", symbols.size());

        // Persist each symbol's data
        symbols.stream()
                .map(this::fetchMarketDataFromCache)
                .filter(Objects::nonNull)
                .forEach(this::persistMarketData);

        log.info("Database refresh completed for {} symbols", symbols.size());
    }

    /**
     * Caches market data response and registers the symbol.
     *
     * @param symbol the stock symbol
     * @param marketData the market data to cache
     * @return Completable indicating cache operation completion
     */
    public Completable cacheMarketData(final String symbol, final MarketDataResponse marketData) {
        return cache.put(symbol, marketData)
                .andThen(cache.putSymbol(symbol))
                .doOnComplete(() -> log.debug("Cached market data for symbol: {}", symbol))
                .doOnError(error -> log.error("Failed to cache data for symbol: {}", symbol, error));
    }

    /**
     * Fetches market data from cache for a given symbol.
     *
     * @param symbol the stock symbol
     * @return MarketDataResponse from cache, or null if not found
     */
    private MarketDataResponse fetchMarketDataFromCache(final String symbol) {
        try {
            return cache.get(symbol).blockingGet();
        } catch (Exception e) {
            log.warn("Failed to fetch data from cache for symbol: {}", symbol, e);
            return null;
        }
    }
}
