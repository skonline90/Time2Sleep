package de.skonline90.Time2Sleep.main;

import java.awt.EventQueue;

import de.skonline90.Time2Sleep.view.Gui;

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
