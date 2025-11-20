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
    private final ResponseValidator validator;

    public MarketDataApiClient(final TwelveDataClient twelveDataClient, final MarketDataMapper mapper,
            final ObjectMapper jsonMapper, @Value("${twelvedata.apiKey}") final String apiKey,
            final ResponseValidator validator) {
        this.twelveDataClient = twelveDataClient;
        this.mapper = mapper;
        this.jsonMapper = jsonMapper;
        this.apiKey = apiKey;
        this.validator = validator;
    }

    public Single<MarketDataResponse> fetchMarketData(final String symbol) {
        return twelveDataClient.getData(apiKey, symbol)
                .flatMap(jsonResponse -> mapResponse(symbol, jsonResponse))
                .doOnError(error -> log.error("Failed to fetch data from API for symbol: {}", symbol, error));
    }

    private Single<MarketDataResponse> mapResponse(final String symbol, final String jsonResponse) {
        try {
            final TwelveDataResponse apiResponse = jsonMapper.readValue(jsonResponse, TwelveDataResponse.class);
            validator.validate(apiResponse, symbol);

            final MarketDataResponse grpcResponse = mapper.toGrpcResponse(apiResponse);
            return Single.just(grpcResponse);

        } catch (Exception e) {
            return Single.error(new MarketDataApiException("Failed to parse API response for symbol: " + symbol, e));
        }
    }
}
