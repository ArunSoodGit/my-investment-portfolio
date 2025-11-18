package com.sood.market.data.domain;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.log4j.Log4j2;

/**
 * Manages market session state and timing logic.
 * Determines whether stock market (NYSE) is currently open or closed.
 * <p>
 * This replaces static methods in MarketSessionMonitor with proper instance methods
 * following SOLID principles and enabling easier testing.
 */
@Singleton
@Log4j2
public class MarketSession {

    private final ZoneId marketTimeZone;
    private final LocalTime marketOpenTime;
    private final LocalTime marketCloseTime;

    public MarketSession(@Value("${market.session.timezone:America/New_York}") final String timezone,
            @Value("${market.session.open-time:09:30}") final String openTime,
            @Value("${market.session.close-time:16:00}") final String closeTime) {
        this.marketTimeZone = ZoneId.of(timezone);
        this.marketOpenTime = LocalTime.parse(openTime);
        this.marketCloseTime = LocalTime.parse(closeTime);

        log.info("MarketSession initialized: timezone={}, open={}, close={}",
                timezone, openTime, closeTime);
    }

    /**
     * Checks if the stock market is currently open.
     * Market is open Monday-Friday between market open and close times.
     *
     * @return true if market is currently open
     */
    public boolean isMarketOpen() {
        final ZonedDateTime now = getCurrentMarketTime();
        final DayOfWeek dayOfWeek = now.getDayOfWeek();
        final LocalTime currentTime = now.toLocalTime();

        final boolean isWeekday = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
        final boolean isDuringMarketHours = !currentTime.isBefore(marketOpenTime)
                && !currentTime.isAfter(marketCloseTime);

        final boolean isOpen = isWeekday && isDuringMarketHours;

        if (log.isDebugEnabled()) {
            log.debug("Market status check: day={}, time={}, open={}",
                    dayOfWeek, currentTime, isOpen);
        }

        return isOpen;
    }

    /**
     * Checks if current time is after market close time.
     * Useful for determining when to save end-of-day snapshots.
     *
     * @return true if current time is after market close
     */
    public boolean isAfterMarketClose() {
        final ZonedDateTime now = getCurrentMarketTime();
        final LocalTime currentTime = now.toLocalTime();

        return currentTime.isAfter(marketCloseTime);
    }

    /**
     * Checks if it's a new day (before 1 AM market time).
     * Used to reset daily flags.
     *
     * @return true if it's before 1 AM in market timezone
     */
    public boolean isNewDay() {
        final ZonedDateTime now = getCurrentMarketTime();
        return now.toLocalTime().isBefore(LocalTime.of(1, 0));
    }

    /**
     * Gets current time in market timezone.
     *
     * @return current ZonedDateTime in market timezone
     */
    public ZonedDateTime getCurrentMarketTime() {
        return ZonedDateTime.now(marketTimeZone);
    }

}
