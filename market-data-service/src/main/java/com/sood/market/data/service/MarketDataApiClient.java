package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sood.market.data.client.TwelveDataClient;
import com.sood.market.data.model.TwelveDataResponse;
import io.micronaut.context.annotation.Value;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

/**
 * Client for fetching market data from external API.
 * Follows Single Responsibility Principle - only handles API communication.
 */
@Singleton
@Log4j2
public class MarketDataApiClient {

    private final TwelveDataClient twelveDataClient;
    private final MarketDataMapper mapper;
    private final ObjectMapper jsonMapper;
    private final String apiKey;

    public MarketDataApiClient(final TwelveDataClient twelveDataClient,
            final MarketDataMapper mapper, final ObjectMapper jsonMapper,
            @Value("${twelvedata.apiKey}") final String apiKey) {
        this.twelveDataClient = twelveDataClient;
        this.mapper = mapper;
        this.jsonMapper = jsonMapper;
        this.apiKey = apiKey;
    }

    /**
     * Fetches market data from TwelveData API.
     *
     * @param symbol the stock symbol
     * @return Single emitting MarketDataResponse
     */
    public Single<MarketDataResponse> fetchMarketData(final String symbol) {
        log.info("Fetching market data from API for symbol: {}", symbol);

        return twelveDataClient.getResponse(apiKey, symbol)
                .flatMap(jsonResponse -> mapResponse(symbol, jsonResponse))
                .doOnSuccess(response -> log.info("Successfully fetched data for {}: price={}, change={}%",
                        symbol, response.getPrice(), response.getPercentageChange()))
                .doOnError(error -> log.error("Failed to fetch data from API for symbol: {}", symbol, error));
    }

    /**
     * Parses JSON response and maps to gRPC format.
     *
     * @param symbol       the stock symbol
     * @param jsonResponse raw JSON response from API
     * @return Single emitting mapped MarketDataResponse
     */
    private Single<MarketDataResponse> mapResponse(final String symbol, final String jsonResponse) {
        try {
            final TwelveDataResponse apiResponse = jsonMapper.readValue(jsonResponse, TwelveDataResponse.class);
            validateApiResponse(apiResponse, symbol);

            final MarketDataResponse grpcResponse = mapper.toGrpcResponse(apiResponse);
            return Single.just(grpcResponse);

        } catch (Exception e) {
            return Single.error(new MarketDataApiException(
                    "Failed to parse API response for symbol: " + symbol, e));
        }
    }

    /**
     * Validates that API response contains required data.
     *
     * @param response the parsed API response
     * @param symbol   the requested symbol
     * @throws MarketDataApiException if validation fails
     */
    private void validateApiResponse(final TwelveDataResponse response, final String symbol) {
        if (response == null) {
            throw new MarketDataApiException("API returned null response for symbol: " + symbol);
        }
        if (response.getSymbol() == null || response.getSymbol().isEmpty()) {
            throw new MarketDataApiException("API response missing symbol field");
        }
        if (response.getClose() == null || response.getClose().isEmpty()) {
            throw new MarketDataApiException("API response missing price data for symbol: " + symbol);
        }
    }

    /**
     * Custom exception for API-related errors.
     */
    public static class MarketDataApiException extends RuntimeException {
        public MarketDataApiException(final String message) {
            super(message);
        }

        public MarketDataApiException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
