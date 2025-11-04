package com.sood.market.data.scheduler;

import com.example.market.grpc.MarketDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sood.market.data.client.TwelveDataClient;
import com.sood.market.data.model.TwelveDataResponse;
import io.micronaut.context.annotation.Value;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MarketDataUpdater {

    private static final Logger log = LoggerFactory.getLogger(MarketDataUpdater.class);
    private static final int MAX_REQUESTS_PER_MINUTE = 8;
    private static final long INTERVAL_MS = 60_000 / MAX_REQUESTS_PER_MINUTE; // ~7.5s

    private final TwelveDataClient twelveDataClient;
    private final MarketDataCacheManager cache;
    private final ObjectMapper objectMapper;
    private final String authHeader;

    public MarketDataUpdater(final TwelveDataClient twelveDataClient, final MarketDataCacheManager cache,
            final ObjectMapper objectMapper, final @Value("${finnhub.token}") String authHeader) {
        this.twelveDataClient = twelveDataClient;
        this.cache = cache;
        this.objectMapper = objectMapper;
        this.authHeader = authHeader;
    }

    /**
     * Aktualizuje cache z API dla wszystkich symboli w zadanym interwale czasowym.
     */
    public Completable updateCacheFromApi(final Set<String> symbols) {
        return Observable.fromIterable(symbols)
                .concatMap(symbol -> fetchAndCache(symbol)
                        .delay(INTERVAL_MS, TimeUnit.MILLISECONDS)
                        .toObservable()
                        .onErrorResumeNext(err -> {
                            log.error("Błąd przy fetchowaniu symbolu {}: {}", symbol, err.getMessage(), err);
                            return Observable.empty();
                        })
                )
                .ignoreElements(); // Zamienia Observable<?> na Completable
    }

    private Single<MarketDataResponse> fetchAndCache(final String symbol) {
        return twelveDataClient.getResponse(authHeader, symbol)
                .flatMap(json -> {
                    try {
                        TwelveDataResponse response = objectMapper.readValue(json, TwelveDataResponse.class);
                        MarketDataResponse grpcResponse = mapToGrpc(response);
                        return cache.put(symbol, grpcResponse)
                                .toSingleDefault(grpcResponse);
                    } catch (Exception e) {
                        return Single.error(e);
                    }
                });
    }

    private MarketDataResponse mapToGrpc(TwelveDataResponse response) {
        return MarketDataResponse.newBuilder()
                .setPrice(response.getClose())
                .setPercentageChange(new BigDecimal(response.getPercent_change())
                        .setScale(2, RoundingMode.HALF_UP)
                        .toString())
                .setCompanyName(response.getName())
                .setExchange(response.getExchange())
                .build();
    }
}