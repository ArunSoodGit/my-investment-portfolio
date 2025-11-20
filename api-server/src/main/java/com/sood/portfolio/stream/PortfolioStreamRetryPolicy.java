package com.sood.portfolio.stream;

import java.util.concurrent.TimeUnit;

public class PortfolioStreamRetryPolicy {
    private static final long RETRY_DELAY_SECONDS = 5;
    private static final TimeUnit RETRY_TIME_UNIT = TimeUnit.SECONDS;

    public long getRetryDelaySeconds() {
        return RETRY_DELAY_SECONDS;
    }

    public TimeUnit getRetryTimeUnit() {
        return RETRY_TIME_UNIT;
    }
}
