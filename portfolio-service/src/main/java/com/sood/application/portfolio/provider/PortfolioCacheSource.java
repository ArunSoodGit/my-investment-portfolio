package com.sood.application.portfolio.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sood.infrastructure.entity.PortfolioEntity;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

/**
 * Redis-based cache for portfolio entities.
 * Provides fast access to portfolio data with automatic expiration (1 hour TTL).
 */
@Singleton
@Log4j2
public class PortfolioCacheSource implements PortfolioSource {

    private static final String PORTFOLIO_KEY_SUFFIX = ":PORTFOLIO";
    private static final long CACHE_TTL_SECONDS = 3600;

    private final RedisCommands<String, String> redis;
    private final ObjectMapper objectMapper;

    public PortfolioCacheSource(@Named("mainRedis") final RedisCommands<String, String> redis,
            final ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves a portfolio from cache.
     *
     * @param portfolioId the portfolio identifier
     * @return the portfolio entity or null if not found or error occurs
     */
    @Override
    public PortfolioEntity get(final Long portfolioId) {
        return getFromRedis(portfolioId, PortfolioEntity.class);
    }

    /**
     * Stores a portfolio in cache with TTL of 1 hour.
     *
     * @param portfolio the portfolio entity to cache
     */
    public void put(final PortfolioEntity portfolio) {
        putToRedis(portfolio.getId(), portfolio);
    }

    private <T> T getFromRedis(final Long key, Class<T> clazz) {
        try {
            final String redisKey = generateRedisKey(key);
            final String value = redis.get(redisKey);
            if (value != null) {
                return objectMapper.readValue(value, clazz);
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("Failed to deserialize Redis value for key: {}", key, e);
        } catch (Exception e) {
            log.error("Unexpected error accessing Redis for key: {}", key, e);
        }
        return null;
    }

    private void putToRedis(final Long key, final Object value) {
        try {
            final String redisKey = generateRedisKey(key);
            final String serializedValue = objectMapper.writeValueAsString(value);
            redis.setex(redisKey, CACHE_TTL_SECONDS, serializedValue);
            log.debug("Cached portfolio with key: {}", key);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("Failed to serialize value for Redis key: {}", key, e);
        } catch (Exception e) {
            log.error("Unexpected error storing to Redis", e);
        }
    }

    private String generateRedisKey(final Long key) {
        return key + PORTFOLIO_KEY_SUFFIX;
    }
}