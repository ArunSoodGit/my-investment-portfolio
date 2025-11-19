package com.sood.domain;

import jakarta.inject.Singleton;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Monitors NYSE market trading sessions.
 * Checks if the current time has passed the end of the NYSE trading day.
 */
@Singleton
public class MarketSessionMonitor {

    private static final ZoneId NEW_YORK_ZONE = ZoneId.of("America/New_York");
    private static final LocalTime NYSE_CLOSE_TIME = LocalTime.of(16, 0);

    /**
     * Determines if the current time is after NYSE market close (4 PM ET).
     *
     * @return true if current time is after market close, false otherwise
     */
    public boolean isEndOfNYSESession() {
        final ZonedDateTime now = ZonedDateTime.now(NEW_YORK_ZONE);
        final LocalTime time = now.toLocalTime();
        return time.isAfter(NYSE_CLOSE_TIME);
    }
}