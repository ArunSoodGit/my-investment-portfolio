package com.sood.market.data.cache;

import com.example.market.grpc.MarketDataResponse;
import com.sood.market.data.exception.CacheOperationException;
import com.sood.market.data.exception.CacheSerializationException;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Value;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class MarketDataCacheManager {

    private final RedisCommands<String, String> redis;
    private final MarketDataSerializer serializer;
    private final CacheKeyGenerator keyGenerator;
    private final long ttlSeconds;

    public MarketDataCacheManager(@Named("mainRedis") final RedisCommands<String, String> redis,
            final MarketDataSerializer serializer, final CacheKeyGenerator keyGenerator,
            @Value("${cache.market-data.ttl-seconds:3600}") final long ttlSeconds) {
        this.redis = redis;
        this.serializer = serializer;
        this.keyGenerator = keyGenerator;
        this.ttlSeconds = ttlSeconds;
    }

    public Completable put(final String symbol, final MarketDataResponse data) {
        return Completable.fromRunnable(() -> {
            validateInput(symbol, data);

            try {
                final String key = keyGenerator.generateMarketDataKey(symbol);
                final String json = serializer.serialize(data);

                redis.setex(key, ttlSeconds, json);

                log.debug("Cached market data for symbol: {} (TTL: {}s)", symbol, ttlSeconds);
            } catch (final CacheSerializationException e) {
                log.error("Serialization error while caching data for symbol: {}", symbol, e);
                throw new CacheOperationException("Failed to serialize market data for: " + symbol, e);
            } catch (Exception e) {
                log.error("Redis error while caching data for symbol: {}", symbol, e);
                throw new CacheOperationException("Failed to store market data in cache for: " + symbol, e);
            }
        });
    }

    public Maybe<MarketDataResponse> get(final String symbol) {
        return Maybe.fromCallable(() -> {
            validateSymbol(symbol);

            try {
                final String key = keyGenerator.generateMarketDataKey(symbol);
                final String value = redis.get(key);

                if (value == null) {
                    log.debug("Cache miss for symbol: {}", symbol);
                    return null;
                }

                log.debug("Cache hit for symbol: {}", symbol);
                return serializer.deserialize(value);

            } catch (final CacheSerializationException e) {
                log.error("Deserialization error for symbol: {}, returning empty", symbol, e);
                return null;
            } catch (final Exception e) {
                log.error("Redis error while reading cache for symbol: {}, returning empty", symbol, e);
                return null;
            }
        });
    }

    public Completable putSymbol(final String symbol) {
        return Completable.fromRunnable(() -> {
            validateSymbol(symbol);

            try {
                final String key = keyGenerator.getSymbolsSetKey();
                redis.sadd(key, symbol.toUpperCase());

            } catch (final Exception e) {
                log.error("Failed to add symbol to tracked set: {}", symbol, e);
                throw new CacheOperationException("Failed to track symbol: " + symbol, e);
            }
        });
    }

    public Single<Set<String>> getAllSymbols() {
        return Single.fromCallable(() -> {
            try {
                final String key = keyGenerator.getSymbolsSetKey();
                final Set<String> symbols = redis.smembers(key);

                log.debug("Retrieved {} tracked symbols from cache", symbols.size());
                return symbols;
            } catch (Exception e) {
                log.error("Failed to retrieve tracked symbols from cache", e);
                throw new CacheOperationException("Failed to retrieve tracked symbols", e);
            }
        });
    }

    private void validateSymbol(final String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
    }

    private void validateInput(final String symbol, final MarketDataResponse data) {
        validateSymbol(symbol);
        if (data == null) {
            throw new IllegalArgumentException("MarketDataResponse cannot be null");
        }
    }
}
