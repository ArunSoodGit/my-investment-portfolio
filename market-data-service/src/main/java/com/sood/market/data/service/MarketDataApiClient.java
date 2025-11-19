package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sood.market.data.client.TwelveDataClient;
import com.sood.market.data.exception.MarketDataApiException;
import com.sood.market.data.model.TwelveDataResponse;
import io.micronaut.context.annotation.Value;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import com.sood.market.data.exception.MarketDataApiException;

/**
 * Client for fetching market data from external API.
 */
@Singleton
@Log4j2
public class MarketDataApiClient {

    private final TwelveDataClient twelveDataClient;
    private final MarketDataMapper mapper;
    private final ObjectMapper jsonMapper;
    private final String apiKey;

    public MarketDataApiClient(final TwelveDataClient twelveDataClient, final MarketDataMapper mapper,
            final ObjectMapper jsonMapper, @Value("${twelvedata.apiKey}") final String apiKey) {
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
        return twelveDataClient.getResponse(apiKey, symbol)
                .flatMap(jsonResponse -> mapResponse(symbol, jsonResponse))
                .doOnError(error -> log.error("Failed to fetch data from API for symbol: {}", symbol, error));
    }

    private Single<MarketDataResponse> mapResponse(final String symbol, final String jsonResponse) {
        try {
            final TwelveDataResponse apiResponse = jsonMapper.readValue(jsonResponse, TwelveDataResponse.class);
            validateApiResponse(apiResponse, symbol);

            final MarketDataResponse grpcResponse = mapper.toGrpcResponse(apiResponse);
            return Single.just(grpcResponse);

        } catch (Exception e) {
            return Single.error(new MarketDataApiException("Failed to parse API response for symbol: " + symbol, e));
        }
    }

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
}
