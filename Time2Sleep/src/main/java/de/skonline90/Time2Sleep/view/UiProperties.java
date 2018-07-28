package de.skonline90.Time2Sleep.view;

import java.awt.Color;
import java.awt.Font;

/**
 * This interface offers constants with reoccuring UI properties.
 * 
 * @author skonline90
 * @version 28.07.18
 */
public interface UiProperties
{
    /*
     * Basic Font Properties
     */
    public static final Font UI_BASIC_TEXT_FONT = new Font("Tahoma", Font.PLAIN,
            15);
    public static final int UI_BASIC_TEXT_FONT_SIZE = 15;

    /*
     * Big Countdown Font Properties
     */
    public static final Font UI_COUNTDOWN_FONT = new Font("Impact", Font.PLAIN,
            50);

    public static final Color UI_BG_COLOR = Color.LIGHT_GRAY;
    public static final Color UI_MENU_BG_COLOR = Color.LIGHT_GRAY;
    public static final Color UI_TEXT_COLOR = Color.black;
}
