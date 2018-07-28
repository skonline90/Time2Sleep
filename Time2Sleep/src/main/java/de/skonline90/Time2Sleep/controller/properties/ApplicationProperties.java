package de.skonline90.Time2Sleep.controller.properties;

import java.io.File;

/**
 * This interface offers constants for reoccuring properties.
 * 
 * @author skonline90
 * @version 28.07.18
 */
public interface ApplicationProperties 
{
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String SETTINGS_FILE_LOCATION = System.getProperty("user.dir") + File.separator + "settings.xml";

}
