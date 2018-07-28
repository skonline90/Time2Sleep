package de.skonline90.Time2Sleep.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.text.DateFormatter;

import de.skonline90.Time2Sleep.controller.CurrentTime;
import de.skonline90.Time2Sleep.controller.properties.ApplicationProperties;

public final class Gui extends JFrame implements Runnable
{
    private static final long serialVersionUID = 1L;

    private static final String GUI_TITLE = "Time2Sleep";
    private static final String GUI_VERSION = "0.0.1";
    private static final String TIME_FORMAT = ApplicationProperties.TIME_FORMAT;

    private JComboBox<String> cBoxSettingSelector;

    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenu mnLanguage;
    private JMenuItem mnitmQuit;

    private JLabel lblBigCountdown;
    private JLabel lblCurrentTimeText;
    private JLabel lblCurrentTimeValue;
    private JLabel lblCountdownText;

    private JButton btnAbort;
    private JButton btnStart;

    private JSpinner spnTimeSelector;

    private TimerTask currentTimeTimerTask;
    private TimerTask countdownTimerTask;
    private long countDownSeconds;

    // =============== START CONSTRUCTOR & INIT METHODS ===============

    public Gui()
    {
        setLayout();
        initMenu();
        initComboBox();
        initLabels();
        initButtons();
        addActionListeners();
        initSpinner();
        startCurrentTimeThread();
        setFrameProperties();
    }

    private void setLayout()
    {
        getContentPane().setLayout(null);

        // Centers the Frame
        int frameWidth = 267;
        int frameHeight = 330;
        Dimension screenSize = Toolkit.getDefaultToolkit()
            .getScreenSize();
        int screenWidth = (int) Math.round(screenSize.getWidth());
        int screenHeight = (int) Math.round(screenSize.getHeight());
        int x = (screenWidth / 2) - (frameWidth / 2);
        int y = (screenHeight / 2) - (frameHeight / 2);
        setBounds(new Rectangle(x, y, frameWidth, frameHeight));
    }

    private void initMenu()
    {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnFile = new JMenu("File");
        menuBar.add(mnFile);

        mnLanguage = new JMenu("Language");
        menuBar.add(mnLanguage);

        mnitmQuit = new JMenuItem("Quit");
        mnFile.add(mnitmQuit);
    }

    private void initComboBox()
    {
        final String[] cboxItems = new String[] {"Shutdown", "Sleep", "Lock",
                "Restart"};

        cBoxSettingSelector = new JComboBox<String>();
        cBoxSettingSelector.setBounds(10, 11, 234, 20);
        for (String item : cboxItems)
        {
            cBoxSettingSelector.addItem(item);
        }
        getContentPane().add(cBoxSettingSelector);
    }

    private void initButtons()
    {
        btnAbort = new JButton("Abort");
        btnAbort.setBounds(84, 239, 76, 23);
        btnAbort.setEnabled(false);
        getContentPane().add(btnAbort);

        btnStart = new JButton("Start");
        btnStart.setBounds(168, 239, 76, 23);
        getContentPane().add(btnStart);
    }

    private void addActionListeners()
    {
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent windowEvent)
            {
                if (countDownSeconds > 0) countdownTimerTask.cancel();
                close();
            }
        });

        mnitmQuit.addActionListener(e -> close());

        btnAbort.addActionListener(e -> {
            btnStart.setEnabled(true);
            btnAbort.setEnabled(false);
            if (countDownSeconds > 0) countdownTimerTask.cancel();
            countDownSeconds = 0;
            lblBigCountdown.setText(setCountdownTimer(0));
        });

        btnStart.addActionListener(e -> {
            setInitialCountdownTimer();

            int choice = -5000;
            if (countDownSeconds == 0)
            {
                choice = Dialogs.showZeroCountdownTimeDialog(this);
                if (choice == JOptionPane.NO_OPTION)
                {
                    return;
                }
            }
            btnStart.setEnabled(false);
            btnAbort.setEnabled(true);
            startCountdown();
        });
    }

    private void initLabels()
    {
        lblBigCountdown = new JLabel("00:00:00");
        lblBigCountdown.setHorizontalAlignment(SwingConstants.CENTER);
        lblBigCountdown.setFont(UiProperties.UI_COUNTDOWN_FONT);
        lblBigCountdown.setBounds(10, 38, 234, 89);
        getContentPane().add(lblBigCountdown);

        lblCurrentTimeText = new JLabel("Current Time");
        lblCurrentTimeText.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCurrentTimeText.setBounds(10, 138, 120, 20);
        getContentPane().add(lblCurrentTimeText);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        lblCurrentTimeValue = new JLabel(formatter.format(LocalTime.now()));
        lblCurrentTimeValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCurrentTimeValue.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCurrentTimeValue.setBounds(140, 138, 104, 20);
        getContentPane().add(lblCurrentTimeValue);

        lblCountdownText = new JLabel("Countdown");
        lblCountdownText.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCountdownText.setBounds(10, 169, 120, 20);
        getContentPane().add(lblCountdownText);
    }

    private void initSpinner()
    {
        SpinnerDateModel spinnerModel = new SpinnerDateModel();

        spnTimeSelector = new JSpinner();
        spnTimeSelector.setBounds(140, 169, 104, 20);
        spnTimeSelector.setModel(spinnerModel);
        getContentPane().add(spnTimeSelector);

        DateEditor editor = new DateEditor(spnTimeSelector, TIME_FORMAT);
        DateFormatter formatter = (DateFormatter) editor.getTextField()
            .getFormatter();
        spnTimeSelector.setEditor(editor);
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        // Sets the initial value on 00:00:00
        Date date = new Date(-60 * 60 * 1000);
        spnTimeSelector.setValue(date);

        spnTimeSelector.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(final KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    System.out.println("Enter");
                    btnStart.doClick();
                }
            }
        });
    }

    // =============== END CONSTRUCTOR & INIT METHODS ===============

    private void startCurrentTimeThread()
    {
        currentTimeTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                lblCurrentTimeValue
                    .setText(CurrentTime.displayFormattedCurrentTime());
            }
        };
        Timer timer = new Timer();
        timer.schedule(currentTimeTimerTask, 0, 1000);
    }

    private void setFrameProperties()
    {
        setVisible(true);
        setTitle(GUI_TITLE + " " + GUI_VERSION);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void close()
    {
        currentTimeTimerTask.cancel();
        if (countDownSeconds > 0) countdownTimerTask.cancel();
        dispose();
        System.exit(0);
    }

    private int getUserInputCountdownInSeconds()
    {
        Date value = (Date) spnTimeSelector.getValue();
        LocalTime time = value.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalTime();
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        return ((hour * 3600) + (minute * 60) + second);
    }

    private void setInitialCountdownTimer()
    {
        countDownSeconds = getUserInputCountdownInSeconds();
    }

    private String setCountdownTimer(int secs)
    {
        //        int seconds = secs;
        //        int hours = (seconds / 3600);
        //        seconds = seconds % 3600;
        //        int minutes = (seconds / 60);
        //        seconds = seconds % 60;

        Duration duration = Duration.ofSeconds(secs);
        LocalTime time = LocalTime.MIDNIGHT;
        return LocalTime.MIDNIGHT.plus(duration)
            .format(DateTimeFormatter
                .ofPattern(ApplicationProperties.TIME_FORMAT));

    }

    private void startCountdown()
    {
        countdownTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                if (countDownSeconds > 0)
                {
                    String countDownAsText = setCountdownTimer(
                            (int) countDownSeconds);
                    lblBigCountdown.setText(countDownAsText);
                    countDownSeconds--;
                }
                else
                {
                    //sleeps computer
                    lblBigCountdown.setText(setCountdownTimer(0));
                    btnStart.setEnabled(true);
                    btnAbort.setEnabled(false);
                    System.out.println("BOOM");
                    //                    try
                    //                    {
                    //                        Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
                    //                    }
                    //                    catch (IOException e)
                    //                    {
                    //                        // TODO Auto-generated catch block
                    //                        e.printStackTrace();
                    //                    }
                    countdownTimerTask.cancel();
                }
            }
        };
        Timer countdownTimer = new Timer();
        countdownTimer.schedule(countdownTimerTask, 0, 1000);
    }

    @Override
    public void run()
    {

    }
}
