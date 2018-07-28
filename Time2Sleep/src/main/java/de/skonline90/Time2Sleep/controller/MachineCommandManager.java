package de.skonline90.Time2Sleep.controller;

import java.io.IOException;

/**
 * This class offers a method to send certain commands to 
 * the PC (fe. Shutdown command).
 * 
 * @author skonline90
 * @version 28.07.18
 */
public class MachineCommandManager 
{
    private String osName;

    /**
     * Constructor.
     */
    public MachineCommandManager()
    {
        osName = System.getProperty("os.name");
    }

    /**
     * Sends a shutdown, sleep, lock or restart command to the
     * computer.
     * 
     * @param command String: shutdown, sleep, lock, restart
     * @throws IOException If IO Error occures.
     */
    public void sendMachineCommand(String command) throws IOException
    {
        if (osName.matches("Windows([\\s\\w\"§$%&/\\(\\)])*"))
        {
            if (command.equals("shutdown"))
            {
                Runtime.getRuntime()
                    .exec("Shutdown.exe -s -t 00");
                return;
            }
            if (command.equals("sleep"))
            {
                Runtime.getRuntime()
                    .exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
                return;
            }
            if (command.equals("lock"))
            {
                Runtime.getRuntime()
                    .exec("Rundll32.exe User32.dll,LockWorkStation");
            }
            if (command.equals("restart"))
            {
                Runtime.getRuntime()
                    .exec("Shutdown.exe -r -t 00");
            }
        }
        else
        {
            System.out.println("Unsupported OS!");
        }
    }
}
