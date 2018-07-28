package de.skonline90.Time2Sleep.view;

import java.awt.Container;

import javax.swing.JOptionPane;

final public class Dialogs
{
    public static int showExitDialog(Container parent)
    {
        int choice = JOptionPane.showConfirmDialog(parent,
                "Do you really want to quit?", "Quit?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        return choice;
    }

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
}
