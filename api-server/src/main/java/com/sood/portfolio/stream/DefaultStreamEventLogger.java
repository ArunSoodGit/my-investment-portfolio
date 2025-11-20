package com.sood.portfolio.stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultStreamEventLogger implements StreamEventLogger {

    @Override
    public void logStreamStarted(Long portfolioId) {
        log.info("Starting portfolio stream subscription (id={})", portfolioId);
    }

    @Override
    public void logStreamError(Long portfolioId, String errorMessage) {
        log.warn("Portfolio stream error (id={}): {}", portfolioId, errorMessage);
    }

    @Override
    public void logStreamCompleted(Long portfolioId) {
        log.info("Portfolio stream completed by server (id={})", portfolioId);
    }

    @Override
    public void logStreamCancelled(Long portfolioId) {
        log.info("Portfolio stream subscription cancelled (id={})", portfolioId);
    }

    @Override
    public void logRetryAttempt(Long portfolioId) {
        log.info("Retrying portfolio stream connection (id={})", portfolioId);
    }
}
