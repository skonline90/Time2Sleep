package de.skonline90.Time2Sleep.view;

import java.awt.Container;

import javax.swing.JOptionPane;

/**
 * This class offers methods that open JOptionPanes. Each JOptionPane
 * is a user information or error message.
 * 
 * @author skonline90
 * @version 28.07.18
 */
final public class Dialogs 
{
    public static int showZeroCountdownTimeDialog(Container parent)
    {
        int choice = JOptionPane.showConfirmDialog(parent,
                "The time is set to 00:00:00. Are you sure you want to proceed?",
                "Zero Time", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        return choice;
    }

    public static void showIoErrorDialog(Container parent)
    {
        JOptionPane.showMessageDialog(parent, "An IO Error occured.",
                "IO Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showLoadSettingsError(Container parent)
    {
        JOptionPane.showMessageDialog(parent, "Couldn't load the default settings.",
                "Default Settings Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showAudioFileError(Container parent)
    {
        JOptionPane.showMessageDialog(parent, "Couldn't load the audio file.",
                "Audio File Error", JOptionPane.ERROR_MESSAGE);
    }
}
