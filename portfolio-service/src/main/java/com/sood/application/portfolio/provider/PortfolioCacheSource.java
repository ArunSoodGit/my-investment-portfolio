package com.sood.application.portfolio.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sood.infrastructure.entity.PortfolioEntity;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioCacheSource implements PortfolioSource {

    private final RedisCommands<String, String> redis;
    private final ObjectMapper objectMapper;

    public PortfolioCacheSource(@Named("mainRedis") final RedisCommands<String, String> redis,
            final ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public PortfolioEntity get(final Long portfolioId) {
        return getFromRedis(portfolioId, PortfolioEntity.class);
    }

    public void put(final PortfolioEntity portfolio) {
        putToRedis(portfolio.getId(), portfolio);
    }

    private <T> T getFromRedis(final Long key, Class<T> clazz) {
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

    private void putToRedis(final Long key, final Object value) {
        try {
            redis.set(key.toString() + "PORTFOLIO", objectMapper.writeValueAsString(value));
        } catch (Exception e) {
            // log error
        }
    }
}