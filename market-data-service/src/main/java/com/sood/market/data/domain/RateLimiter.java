package com.sood.market.data.domain;

import io.micronaut.context.annotation.Value;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Singleton;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;

/**
 * Manages API rate limiting to comply with external API restrictions.
 * Applies delays between requests to avoid exceeding rate limits.
 * <p>
 * This is configurable through application.yml, replacing hardcoded values
 * and following best practices for external API integration.
 */
@Singleton
@Log4j2
public class RateLimiter {

    private final long delayBetweenRequestsMs;

    public RateLimiter(@Value("${api.rate-limit.max-per-minute:8}") final int maxRequestsPerMinute) {

        if (maxRequestsPerMinute <= 0) {
            throw new IllegalArgumentException("Max requests per minute must be positive");
        }

        this.delayBetweenRequestsMs = calculateDelay(maxRequestsPerMinute);

        log.info("RateLimiter initialized: max {} requests/min, delay {}ms between requests",
                maxRequestsPerMinute, delayBetweenRequestsMs);
    }

    /**
     * Applies rate limiting to an Observable stream.
     * Adds delay between emissions to comply with rate limits.
     *
     * @param source the source Observable
     * @param <T>    the type of items emitted
     * @return Observable with rate limiting applied
     */
    public <T> Observable<T> applyRateLimit(final Observable<T> source) {
        return source.concatMap(item ->
                Observable.just(item)
                        .delay(delayBetweenRequestsMs, TimeUnit.MILLISECONDS)
                        .doOnNext(i -> log.trace("Rate limit applied, delayed {}ms", delayBetweenRequestsMs))
        );
    }

    /**
     * Calculates the delay between requests based on max requests per minute.
     *
     * @param maxRequestsPerMinute the maximum requests allowed per minute
     * @return delay in milliseconds
     */
    private long calculateDelay(final int maxRequestsPerMinute) {
        final long minuteInMs = 60_000L;
        return minuteInMs / maxRequestsPerMinute;
    }
}
