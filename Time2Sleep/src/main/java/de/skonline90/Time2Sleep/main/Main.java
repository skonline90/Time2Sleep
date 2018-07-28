package de.skonline90.Time2Sleep.main;

import java.awt.EventQueue;

import de.skonline90.Time2Sleep.view.Gui;

/**
 * The startup class.
 * 
 * @author skonline90
 * @version 28.07.18
 */
public class Main
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new Gui();
            }
        });
    }
}
