package com.sood.market.data.scheduler;

import com.example.market.grpc.MarketDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import io.lettuce.core.api.sync.RedisCommands;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MarketDataCacheManager {

    private static final Logger log = LoggerFactory.getLogger(MarketDataCacheManager.class);

    private final RedisCommands<String, String> redis;
    private final ObjectMapper objectMapper;

    public MarketDataCacheManager(@Named("mainRedis") final RedisCommands<String, String> redis,
            final ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public Completable put(final String symbol, final MarketDataResponse data) {
        return Completable.fromRunnable(() -> {
            try {
                String json = JsonFormat.printer().includingDefaultValueFields().print(data);
                redis.set("market:" + symbol, json);
            } catch (Exception e) {
                log.error("Błąd zapisu do cache dla symbolu {}: {}", symbol, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    public Maybe<MarketDataResponse> get(final String symbol) {
        return Maybe.fromCallable(() -> {
            String value = redis.get("market:" + symbol);
            if (value == null) return null;

            MarketDataResponse.Builder builder = MarketDataResponse.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(value, builder);
            return builder.build();
        });
    }

    // Zapisuje symbol do zbioru wszystkich symboli
    public Completable putSymbol(final String symbol) {
        return Completable.fromRunnable(() -> {
            try {
                redis.sadd("market:all_symbols", symbol); // tutaj SADD jest poprawne
            } catch (Exception e) {
                log.error("Błąd zapisu symbolu do cache: {}", symbol, e);
                throw new RuntimeException(e);
            }
        });
    }

    // Pobiera wszystkie symbole
    public Single<Set<String>> getAllSymbols() {
        return Single.fromCallable(() -> redis.smembers("market:all_symbols"));
    }
}