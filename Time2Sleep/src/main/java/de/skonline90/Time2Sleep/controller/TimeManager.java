package de.skonline90.Time2Sleep.controller;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import de.skonline90.Time2Sleep.controller.properties.ApplicationProperties;

/**
 * This class offers methods to create strings of time.
 * 
 * @author skonline90
 * @version 28.07.18
 */
public class TimeManager
{
    /**
     * Returns the current time as a string.
     */
    public static String displayFormattedCurrentTime()
    {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern(ApplicationProperties.TIME_FORMAT);
        return now.format(formatter);
    }

    /**
     * Returns the actiontime. It is calculated by adding the
     * input time to the current time.
     */
    public static String displayActionTime(LocalTime time)
    {
        LocalTime now = LocalTime.now();
        Duration duration = Duration.ofSeconds(time.getHour() * 3600
                + time.getMinute() * 60 + time.getSecond());
        LocalTime actionTime = now.plus(duration);
        DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern(ApplicationProperties.TIME_FORMAT);
        return formatter.format(actionTime);
    }
}
