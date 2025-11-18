package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sood.market.data.client.TwelveDataClient;
import com.sood.market.data.infrastructure.MarketDataRepository;
import com.sood.market.data.infrastructure.entity.MarketDataEntity;
import com.sood.market.data.model.TwelveDataResponse;
import com.sood.market.data.scheduler.MarketDataCacheManager;
import io.micronaut.context.annotation.Value;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class MarketDataService {

    private final MarketDataCacheManager cache;
    private final MarketDataRepository repository;
    private final TwelveDataClient twelveDataClient;
    private final String authHeader;

    private final ObjectMapper objectMapper;

    public MarketDataService(final MarketDataCacheManager cache, final MarketDataRepository repository,
            final TwelveDataClient twelveDataClient, @Value("${twelvedata.apiKey}") final String authHeader,
            final ObjectMapper objectMapper) {
        this.cache = cache;
        this.repository = repository;
        this.twelveDataClient = twelveDataClient;
        this.authHeader = authHeader;
        this.objectMapper = objectMapper;
    }

    public void refreshMarketData() {
        repository.deleteAll();
        final Set<String> symbols = cache.getAllSymbols().blockingGet();
        symbols.stream()
                .map(symbol -> cache.get(symbol).blockingGet())
                .filter(Objects::nonNull)
                .forEach(data -> {
                    try {
                        final MarketDataEntity entity = new MarketDataEntity();
                        entity.setCompanyName(data.getCompanyName());
                        entity.setSymbol(data.getSymbol());
                        entity.setExchange(data.getExchange());
                        entity.setCurrentPrice(data.getPrice());
                        entity.setPercentageChange(data.getPercentageChange());
                        entity.setCreatedAt(LocalDateTime.now().withNano(0).withSecond(0));
                        repository.save(entity);
                    } catch (Exception e) {
                    }
                });
    }

    public Single<MarketDataResponse> getMarketData(final String symbol) {
        return cache.get(symbol)
                .switchIfEmpty(fetchFromAPI(symbol));
    }

    private Single<MarketDataResponse> fetchFromAPI(final String symbol) {
        return twelveDataClient.getResponse(authHeader, symbol)
                // Debug: pokaż, że subskrypcja ruszyła
                .doOnSubscribe(d -> log.info("Rozpoczynam pobieranie danych dla {}", symbol))

                .flatMap(json -> {
                    try {
                        // mapowanie JSON -> obiekt domenowy
                        final TwelveDataResponse response = objectMapper.readValue(json, TwelveDataResponse.class);
                        final MarketDataResponse grpcResponse = mapToGrpc(response);

                        // zapis do cache w sposób reaktywny
                        return cache.put(symbol, grpcResponse)
                                .andThen(cache.putSymbol(symbol))
                                .andThen(Single.just(grpcResponse));

                    } catch (Exception e) {
                        return Single.error(new RuntimeException("Błąd mapowania danych z API", e));
                    }
                })
                // fallback: pobierz z cache w razie błędu API
                .onErrorResumeNext(error -> {
                    log.warn("Błąd pobierania z API dla {}: {}", symbol, error.getMessage());
                    return cache.get(symbol)
                            .switchIfEmpty(Single.error(new RuntimeException(
                                    "Brak danych w cache i błąd przy pobieraniu z API dla: " + symbol, error
                            )));
                })
                // debug: pokaz wynik
                .doOnSuccess(r -> log.info("Dane dla {} pobrane: {}", symbol, r))
                .doOnError(e -> log.error("Błąd pobierania danych dla {}: {}", symbol, e.getMessage(), e));
    }

    private MarketDataResponse mapToGrpc(final TwelveDataResponse response) {
        return MarketDataResponse.newBuilder()
                .setPrice(response.getClose())
                .setPercentageChange(new BigDecimal(response.getPercent_change())
                        .setScale(2, RoundingMode.HALF_UP)
                        .toString())
                .setSymbol(response.getSymbol())
                .setCompanyName(response.getName())
                .setExchange(response.getExchange())
                .build();
    }
}