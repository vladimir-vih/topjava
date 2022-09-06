package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
    }

    public static String dateToString(LocalDateTime dateTime) {
        return FORMATTER.format(dateTime);
    }

    public static LocalDateTime getCurrentDateTime(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }
}
