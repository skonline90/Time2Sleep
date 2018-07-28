package de.skonline90.Time2Sleep.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import de.skonline90.Time2Sleep.controller.properties.ApplicationProperties;

public class CurrentTime
{
    public static String displayFormattedCurrentTime()
    {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern(ApplicationProperties.TIME_FORMAT);
        return now.format(formatter);
    }
}
