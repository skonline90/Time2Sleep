package de.skonline90.Time2Sleep.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
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

import de.skonline90.Time2Sleep.controller.TimeManager;
import de.skonline90.Time2Sleep.controller.GuiStates;
import de.skonline90.Time2Sleep.controller.MachineCommandManager;
import de.skonline90.Time2Sleep.controller.properties.ApplicationProperties;
import javax.swing.JTextField;
import java.awt.Font;

public final class Gui extends JFrame
{
    private static final long serialVersionUID = 1L;

    private static final String GUI_TITLE = "Time2Sleep";
    private static final String GUI_VERSION = "0.0.1";
    private static final String TIME_FORMAT = ApplicationProperties.TIME_FORMAT;

    private JComboBox<String> cBoxSettingSelector;

    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mnitmQuit;

    private JLabel lblBigCountdown;
    private JLabel lblCurrentTimeText;
    private JLabel lblCurrentTimeValue;
    private JLabel lblCountdownText;
    private JLabel lblActionText;
    private JLabel lblActionValue;

    private JButton btnAbort;
    private JButton btnStart;
    private JButton btnPlus;
    private JButton btnMinus;

    private JSpinner spnTimeSelector;
    private JTextField txtFldAmount;

    private TimerTask currentTimeTimerTask;
    private TimerTask countdownTimerTask;
    private long countDownSeconds;
    private MachineCommandManager machineCommandManager;
    private GuiStates guiState;

    // =============== START CONSTRUCTOR & INIT METHODS ===============

    public Gui()
    {
        setLayout();
        initMenu();
        initComboBox();
        initLabels();
        initButtons();
        initTextField();
        addActionListeners();
        initSpinner();
        startCurrentTimeThread();

        machineCommandManager = new MachineCommandManager();
        guiState = GuiStates.INITIAL;
        setGuiToState(guiState);

        setFrameProperties();
    }

    private void setLayout()
    {
        getContentPane().setLayout(null);

        // Centers the Frame
        int frameWidth = 267;
        int frameHeight = 410;
        Dimension screenSize = Toolkit.getDefaultToolkit()
            .getScreenSize();
        int screenWidth = (int) Math.round(screenSize.getWidth());
        int screenHeight = (int) Math.round(screenSize.getHeight());
        int x = (screenWidth / 2) - (frameWidth / 2);
        int y = (screenHeight / 2) - (frameHeight / 2);
        setBounds(new Rectangle(x, y, frameWidth, frameHeight));
        //        setResizable(false);
    }

    private void initMenu()
    {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnFile = new JMenu("File");
        menuBar.add(mnFile);

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
        btnAbort.setBounds(87, 319, 76, 23);
        getContentPane().add(btnAbort);

        btnStart = new JButton("Start");
        btnStart.setBounds(171, 319, 76, 23);
        getContentPane().add(btnStart);

        btnPlus = new JButton("+");
        btnPlus.setBounds(200, 138, 41, 28);
        getContentPane().add(btnPlus);

        btnMinus = new JButton("-");
        btnMinus.setBounds(10, 138, 41, 28);
        getContentPane().add(btnMinus);

    }

    private void initTextField()
    {
        txtFldAmount = new JTextField();
        txtFldAmount.setBounds(81, 138, 86, 20);
        getContentPane().add(txtFldAmount);
        txtFldAmount.setColumns(10);
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
            guiState = GuiStates.ABORTED;
            setGuiToState(guiState);

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
                if (choice == JOptionPane.NO_OPTION
                        || choice == JOptionPane.CLOSED_OPTION)
                {
                    return;
                }
            }
            guiState = GuiStates.RUNNING;
            setGuiToState(guiState);

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
        lblCurrentTimeText.setBounds(10, 219, 120, 20);
        getContentPane().add(lblCurrentTimeText);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        lblCurrentTimeValue = new JLabel(formatter.format(LocalTime.now()));
        lblCurrentTimeValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCurrentTimeValue.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCurrentTimeValue.setBounds(140, 219, 104, 20);
        getContentPane().add(lblCurrentTimeValue);

        lblCountdownText = new JLabel("Countdown");
        lblCountdownText.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCountdownText.setBounds(10, 188, 120, 20);
        getContentPane().add(lblCountdownText);

        lblActionText = new JLabel("Action");
        lblActionText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblActionText.setBounds(10, 250, 120, 20);
        getContentPane().add(lblActionText);

        lblActionValue = new JLabel("20:24:01");
        lblActionValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblActionValue.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblActionValue.setBounds(140, 250, 104, 20);
        getContentPane().add(lblActionValue);
    }

    private void initSpinner()
    {
        SpinnerDateModel spinnerModel = new SpinnerDateModel();

        spnTimeSelector = new JSpinner();
        spnTimeSelector.setBounds(140, 188, 104, 20);
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

    private void setGuiToState(GuiStates state)
    {
        if (state == GuiStates.INITIAL || state == GuiStates.ABORTED
                || state == GuiStates.COMPLETED)
        {
            btnAbort.setEnabled(false);
            btnStart.setEnabled(true);
            btnPlus.setEnabled(false);
            btnMinus.setEnabled(false);
            cBoxSettingSelector.setEnabled(true);
            spnTimeSelector.setEnabled(true);
        }
        else if (state == GuiStates.RUNNING)
        {
            btnAbort.setEnabled(true);
            btnStart.setEnabled(false);
            btnPlus.setEnabled(true);
            btnMinus.setEnabled(true);
            cBoxSettingSelector.setEnabled(false);
            spnTimeSelector.setEnabled(false);
        }
    }

    private void startCurrentTimeThread()
    {
        currentTimeTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                lblCurrentTimeValue
                    .setText(TimeManager.displayFormattedCurrentTime());
                if (guiState == GuiStates.RUNNING)
                {
                    String countDown = lblBigCountdown.getText();
                    DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern(ApplicationProperties.TIME_FORMAT);
                    LocalTime parsedTime = LocalTime.parse(countDown,
                            formatter);
                    lblActionValue
                        .setText(TimeManager.displayActionTime(parsedTime));
                }
                else
                {
                    Date value = (Date) spnTimeSelector.getValue();
                    LocalTime selectedTime = LocalDateTime
                        .ofInstant(value.toInstant(), ZoneId.systemDefault())
                        .toLocalTime();
                    lblActionValue
                        .setText(TimeManager.displayActionTime(selectedTime));
                }
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
        Duration duration = Duration.ofSeconds(secs);
        return LocalTime.MIDNIGHT.plus(duration)
            .format(DateTimeFormatter
                .ofPattern(ApplicationProperties.TIME_FORMAT));
    }

    private void startCountdown()
    {
        Gui gui = this;
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
                    guiState = GuiStates.COMPLETED;
                    setGuiToState(guiState);
                    lblBigCountdown.setText(setCountdownTimer(0));
                    countdownTimerTask.cancel();
                    String selectedSetting = ((cBoxSettingSelector
                        .getSelectedItem()).toString()).toLowerCase();
                    try
                    {
                        System.out.println(selectedSetting);
                        machineCommandManager
                            .sendMachineCommand(selectedSetting);
                    }
                    catch (IOException e)
                    {
                        Dialogs.showIoErrorDialog(gui);
                        e.printStackTrace();
                    }
                }
            }
        };
        Timer countdownTimer = new Timer();
        countdownTimer.schedule(countdownTimerTask, 0, 1000);
    }
}
