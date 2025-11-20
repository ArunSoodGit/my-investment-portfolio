package com.sood.portfolio.stream;

public interface StreamEventLogger {
    void logStreamStarted(Long portfolioId);
    void logStreamError(Long portfolioId, String errorMessage);
    void logStreamCompleted(Long portfolioId);
    void logStreamCancelled(Long portfolioId);
    void logRetryAttempt(Long portfolioId);
}
