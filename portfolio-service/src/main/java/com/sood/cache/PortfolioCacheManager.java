package com.sood.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.service.PortfolioService;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class PortfolioCacheManager {

    private final RedisCommands<String, String> redis;
    private final ObjectMapper objectMapper;
    private final PortfolioService portfolioService;

    public PortfolioCacheManager(@Named("mainRedis") final RedisCommands<String, String> redis,
            final ObjectMapper objectMapper, final PortfolioService portfolioService) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.portfolioService = portfolioService;
    }

    public PortfolioEntity get(final Long portfolioId) {
        return Optional.ofNullable(getFromRedis(portfolioId, PortfolioEntity.class))
                .orElseGet(() -> fromDb(portfolioId));
    }

    public void put(final PortfolioEntity portfolio) {
        putToRedis(portfolio.getId(), portfolio);
    }

    private <T> T getFromRedis(Long key, Class<T> clazz) {
        try {
            String value = redis.get(key.toString() + "PORTFOLIO");
            if (value != null) {
                return objectMapper.readValue(value, clazz);
            }
        } catch (Exception e) {
            // log error
        }
        return null;
    }

    private void putToRedis(Long key, Object value) {
        try {
            redis.set(key.toString() + "PORTFOLIO", objectMapper.writeValueAsString(value));
        } catch (Exception e) {
            // log error
        }
    }

    private PortfolioEntity fromDb(final Long portfolioId) {
        final PortfolioEntity portfolio = portfolioService.getPortfolio(portfolioId);
        put(portfolio);
        return portfolio;
    }
}