package com.sood.market.data.domain;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class RateLimiter {

    private final long delayMs;

    public RateLimiter(@Value("${api.rate-limit.max-per-minute:8}") int maxRequestsPerMinute) {
        if (maxRequestsPerMinute <= 0) {
            throw new IllegalArgumentException("Max requests per minute must be positive");
        }

        this.delayMs = 60_000L / maxRequestsPerMinute;

        log.info("RateLimiter initialized: max {} requests/min, delay {}ms",
                maxRequestsPerMinute, delayMs);
    }

    public long getDelayMs() {
        return delayMs;
    }
}