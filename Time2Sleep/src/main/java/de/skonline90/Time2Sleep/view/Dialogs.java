package de.skonline90.Time2Sleep.view;

import java.awt.Container;

import javax.swing.JOptionPane;

final public class Dialogs
{
    public static int showExitDialog(Container parent)
    {
        int choice = JOptionPane.showConfirmDialog(parent, "Do you really want to quit?",
                "Quit?", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        return choice;
    }
}
