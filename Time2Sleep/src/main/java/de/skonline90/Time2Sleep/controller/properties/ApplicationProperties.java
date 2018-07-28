package de.skonline90.Time2Sleep.controller.properties;

import java.io.File;

public interface ApplicationProperties
{
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String SETTINGS_FILE_LOCATION = System.getProperty("user.dir") + File.separator + "settings.xml";

}
