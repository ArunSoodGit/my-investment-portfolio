package com.sood.market.data.service;

import com.example.market.grpc.MarketDataResponse;
import com.sood.market.data.infrastructure.entity.MarketDataEntity;
import com.sood.market.data.model.TwelveDataResponse;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Maps market data between different representations (API, gRPC, Entity).
 * Follows Single Responsibility Principle - only handles data transformation.
 */
@Singleton
public class MarketDataMapper {

    private static final int PERCENTAGE_SCALE = 2;
    private static final RoundingMode PERCENTAGE_ROUNDING = RoundingMode.HALF_UP;

    /**
     * Maps TwelveData API response to gRPC response format.
     *
     * @param apiResponse the API response from TwelveData
     * @return gRPC MarketDataResponse
     */
    public MarketDataResponse toGrpcResponse(final TwelveDataResponse apiResponse) {
        return MarketDataResponse.newBuilder()
                .setPrice(apiResponse.getClose())
                .setPercentageChange(formatPercentageChange(apiResponse.getPercent_change()))
                .setSymbol(apiResponse.getSymbol())
                .setCompanyName(apiResponse.getName())
                .setExchange(apiResponse.getExchange())
                .build();
    }

    /**
     * Maps gRPC response to database entity.
     *
     * @param grpcResponse the gRPC response
     * @return MarketDataEntity for persistence
     */
    public MarketDataEntity toEntity(final MarketDataResponse grpcResponse) {
        final MarketDataEntity entity = new MarketDataEntity();
        entity.setCompanyName(grpcResponse.getCompanyName());
        entity.setSymbol(grpcResponse.getSymbol());
        entity.setExchange(grpcResponse.getExchange());
        entity.setCurrentPrice(grpcResponse.getPrice());
        entity.setPercentageChange(grpcResponse.getPercentageChange());
        entity.setCreatedAt(truncateToMinutes(LocalDateTime.now()));
        return entity;
    }

    /**
     * Formats percentage change to 2 decimal places.
     *
     * @param percentageChange raw percentage value
     * @return formatted percentage as string
     */
    private String formatPercentageChange(final String percentageChange) {
        return new BigDecimal(percentageChange)
                .setScale(PERCENTAGE_SCALE, PERCENTAGE_ROUNDING)
                .toString();
    }

    /**
     * Truncates timestamp to minute precision for consistent querying.
     *
     * @param timestamp the original timestamp
     * @return timestamp truncated to minutes
     */
    private LocalDateTime truncateToMinutes(final LocalDateTime timestamp) {
        return timestamp.truncatedTo(ChronoUnit.MINUTES);
    }
}
