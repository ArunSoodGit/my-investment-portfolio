package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.sood.market.data.cache.MarketDataCacheManager;
import com.sood.market.data.infrastructure.MarketDataRepository;
import com.sood.market.data.infrastructure.entity.MarketDataEntity;
import io.reactivex.rxjava3.core.Completable;
import jakarta.inject.Singleton;
import java.util.Objects;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

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

    public MarketDataPersistenceService(final MarketDataRepository repository, final MarketDataCacheManager cache,
            final MarketDataMapper mapper) {
        this.repository = repository;
        this.cache = cache;
        this.mapper = mapper;
    }

    public void persistMarketData(final MarketDataResponse marketData) {
        final MarketDataEntity entity = mapper.toEntity(marketData);
        repository.save(entity);
    }

    public void refreshDatabaseFromCache() {
        repository.deleteAll();

        log.debug("Cleared existing market data from database");

        final Set<String> symbols = cache.getAllSymbols().blockingGet();

        symbols.stream()
                .map(this::fetchMarketDataFromCache)
                .filter(Objects::nonNull)
                .forEach(this::persistMarketData);
    }

    public Completable cacheMarketData(final String symbol, final MarketDataResponse marketData) {
        return cache.put(symbol, marketData)
                .andThen(cache.putSymbol(symbol))
                .doOnError(error -> log.error("Failed to cache data for symbol: {}", symbol, error));
    }

    private MarketDataResponse fetchMarketDataFromCache(final String symbol) {
        try {
            return cache.get(symbol).blockingGet();
        } catch (Exception e) {
            log.warn("Failed to fetch data from cache for symbol: {}", symbol, e);
            return null;
        }
    }
}
