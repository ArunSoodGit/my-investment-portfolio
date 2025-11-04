package com.sood;

import jakarta.inject.Singleton;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Singleton
public class MarketSessionMonitor {

    private static final ZoneId NEW_YORK_ZONE = ZoneId.of("America/New_York");

    public static boolean isNYSEOpen() {
        final ZonedDateTime now = ZonedDateTime.now(NEW_YORK_ZONE);
        final DayOfWeek day = now.getDayOfWeek();
        final LocalTime time = now.toLocalTime();

        return day != DayOfWeek.SATURDAY &&
                day != DayOfWeek.SUNDAY &&
                !time.isBefore(LocalTime.of(9, 30)) &&
                !time.isAfter(LocalTime.of(16, 0));
    }

    public static boolean isEndOfNYSESession() {
        final ZonedDateTime now = ZonedDateTime.now(NEW_YORK_ZONE);
        final LocalTime time = now.toLocalTime();
        return time.isAfter(LocalTime.of(16, 0));
    }

    public static boolean isNewDay() {
        final ZonedDateTime now = ZonedDateTime.now(NEW_YORK_ZONE);
        return now.toLocalTime().isBefore(LocalTime.of(1, 0));
    }
}