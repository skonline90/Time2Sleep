package de.skonline90.Time2Sleep.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.text.DateFormatter;

import de.skonline90.Time2Sleep.controller.GuiStates;
import de.skonline90.Time2Sleep.controller.MachineCommandManager;
import de.skonline90.Time2Sleep.controller.TimeManager;
import de.skonline90.Time2Sleep.controller.properties.ApplicationProperties;
import de.skonline90.Time2Sleep.controller.xml.SleepTimerSettingsXmlSaveFileCreator;
import de.skonline90.Time2Sleep.controller.xml.XmlSettingsReader;
import javax.swing.JSlider;

/**
 * The main application window.
 * 
 * @author skonline90
 * @version 28.07.18
 */
public final class Gui extends JFrame
{
    private static final long serialVersionUID = 1L;

    private static final String GUI_TITLE = "Time2Sleep";
    private static final String GUI_VERSION = "1.1.0";
    private static final String TIME_FORMAT = ApplicationProperties.TIME_FORMAT;

    private JComboBox<String> cBoxSettingSelector;

    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenu mnAudio;
    private JMenuItem mnitmQuit;
    private ButtonGroup btGrAudio;
    private JRadioButtonMenuItem rmnitmNoice;
    private JRadioButtonMenuItem rmnitmAlarm;

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
    private JFormattedTextField txtFldAmount;
    private JSlider slider;

    private TimerTask currentTimeTimerTask;
    private TimerTask countdownTimerTask;
    private Timer countdownTimer;
    private long countDownSeconds;
    private MachineCommandManager machineCommandManager;
    private GuiStates guiState;

    // =============== START CONSTRUCTOR & INIT METHODS ===============

    /**
     * Constructor. Initializes all Swing elements, sets the layout and
     * loads the settings.
     */
    public Gui()
    {
        setLayout();
        initMenu();
        initComboBox();
        initLabels();
        initButtons();
        initTextField();
        initSlider();
        addActionListeners();
        initSpinner();
        loadDefaultSettings();
        startTimeThread();

        machineCommandManager = new MachineCommandManager();
        guiState = GuiStates.INITIAL;
        setGuiToState(guiState);

        setFrameProperties();
    }

    /**
     * Sets the Layout of the Window.
     */
    private void setLayout()
    {
        getContentPane().setLayout(null);

        // Centers the Frame
        int frameWidth = 260;
        int frameHeight = 450;
        Dimension screenSize = Toolkit.getDefaultToolkit()
            .getScreenSize();
        int screenWidth = (int) Math.round(screenSize.getWidth());
        int screenHeight = (int) Math.round(screenSize.getHeight());
        int x = (screenWidth / 2) - (frameWidth / 2);
        int y = (screenHeight / 2) - (frameHeight / 2);
        setBounds(new Rectangle(x, y, frameWidth, frameHeight));
        setResizable(false);
    }

    /**
     * Initializes the menu and its items.
     */
    private void initMenu()
    {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnFile = new JMenu("File");
        menuBar.add(mnFile);

        mnAudio = new JMenu("Audio");
        menuBar.add(mnAudio);

        mnitmQuit = new JMenuItem("Quit");
        mnFile.add(mnitmQuit);

        btGrAudio = new ButtonGroup();
        rmnitmAlarm = new JRadioButtonMenuItem("Alarm");
        rmnitmAlarm.setSelected(true);
        btGrAudio.add(rmnitmAlarm);
        mnAudio.add(rmnitmAlarm);

        rmnitmNoice = new JRadioButtonMenuItem("Noice");
        btGrAudio.add(rmnitmNoice);
        mnAudio.add(rmnitmNoice);
    }

    /**
     * Initializes the combo box element.
     */
    private void initComboBox()
    {
        final String[] cboxItems = new String[] {"Shutdown", "Sleep", "Lock",
                "Restart", "Alarm"};

        cBoxSettingSelector = new JComboBox<String>();
        cBoxSettingSelector.setBounds(10, 11, 234, 20);
        for (String item : cboxItems)
        {
            cBoxSettingSelector.addItem(item);
        }
        getContentPane().add(cBoxSettingSelector);
    }

    /**
     * Initializes all buttons.
     */
    private void initButtons()
    {
        btnAbort = new JButton("Abort");
        btnAbort.setBounds(87, 369, 76, 23);
        getContentPane().add(btnAbort);

        btnStart = new JButton("Start");
        btnStart.setBounds(171, 369, 76, 23);
        getContentPane().add(btnStart);

        btnPlus = new JButton("+");
        btnPlus.setBounds(200, 138, 41, 28);
        getContentPane().add(btnPlus);

        btnMinus = new JButton("-");
        btnMinus.setBounds(10, 138, 41, 28);
        getContentPane().add(btnMinus);

    }

    /**
     * Initializes all textfields.
     */
    private void initTextField()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(
                ApplicationProperties.TIME_FORMAT);
        txtFldAmount = new JFormattedTextField(formatter);
        txtFldAmount.setBounds(81, 138, 86, 20);
        txtFldAmount.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(txtFldAmount);
        LocalDateTime now = LocalDateTime.of(LocalDate.now(),
                LocalTime.of(0, 10, 0));
        ZonedDateTime atZone = now.atZone(ZoneId.systemDefault());
        Date date = Date.from(atZone.toInstant());
        txtFldAmount.setValue(date);
        txtFldAmount.setEditable(false);

        txtFldAmount.setColumns(10);
    }

    private void initSlider()
    {
        slider = new JSlider(0, 100, 0);
        slider.setBounds(10, 177, 231, 26);
        slider.setBackground(UiProperties.UI_BG_COLOR);
        getContentPane().add(slider);

        slider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                double value = (double) slider.getValue();
                // 1200 refers to the maximum amount of seconds
                int seconds = (int) Math.round(((value / 100) * 1190) + 10);
                int hours = seconds / 3600;
                seconds = seconds % 3600;
                int minutes = seconds / 60;
                seconds = seconds % 60;

                LocalDateTime now = LocalDateTime.of(LocalDate.now(),
                        LocalTime.of(hours, minutes, seconds));
                ZonedDateTime atZone = now.atZone(ZoneId.systemDefault());
                Date date = Date.from(atZone.toInstant());

                txtFldAmount.setValue(date);
            }
        });
    }

    /**
     * Adds the ActionListeners for the buttons.
     */
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

        btnPlus.addActionListener(e -> {
            if (guiState == GuiStates.RUNNING)
            {
                Date timeObject = (Date) txtFldAmount.getValue();
                LocalTime userTimeIncrement = LocalDateTime
                    .ofInstant(timeObject.toInstant(), ZoneId.systemDefault())
                    .toLocalTime();
                String currenTimeLeft = lblBigCountdown.getText();
                DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern(ApplicationProperties.TIME_FORMAT);
                LocalTime parsedTime = LocalTime.parse(currenTimeLeft,
                        formatter);
                Duration duration = Duration
                    .ofSeconds(userTimeIncrement.getHour() * 3600
                            + userTimeIncrement.getMinute() * 60
                            + userTimeIncrement.getSecond());

                long totalSecondsInParsedTimeAndDuration = parsedTime.getHour()
                        * 3600 + parsedTime.getMinute() * 60
                        + parsedTime.getSecond() + duration.getSeconds();
                int maxSecondsInFullTimer = 23 * 3600 + 59 * 60 + 59;
                if (totalSecondsInParsedTimeAndDuration <= maxSecondsInFullTimer)
                {
                    LocalTime incrementedTime = parsedTime.plus(duration);
                    lblBigCountdown.setText(formatter.format(incrementedTime));
                    countDownSeconds = incrementedTime.getSecond()
                            + incrementedTime.getMinute() * 60
                            + incrementedTime.getHour() * 3600;
                }
            }
        });

        btnMinus.addActionListener(e -> {
            if (guiState == GuiStates.RUNNING)
            {
                Date timeObject = (Date) txtFldAmount.getValue();
                LocalTime userTimeIncrement = LocalDateTime
                    .ofInstant(timeObject.toInstant(), ZoneId.systemDefault())
                    .toLocalTime();
                String currenTimeLeft = lblBigCountdown.getText();
                DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern(ApplicationProperties.TIME_FORMAT);
                LocalTime parsedTime = LocalTime.parse(currenTimeLeft,
                        formatter);
                Duration duration = Duration
                    .ofSeconds(userTimeIncrement.getHour() * 3600
                            + userTimeIncrement.getMinute() * 60
                            + userTimeIncrement.getSecond());

                int totalSecondsInParsedTime = parsedTime.getHour() * 3600
                        + parsedTime.getMinute() * 60 + parsedTime.getSecond();
                if (totalSecondsInParsedTime > duration.getSeconds())
                {
                    LocalTime decrementedTime = parsedTime.minus(duration);
                    lblBigCountdown.setText(formatter.format(decrementedTime));
                    countDownSeconds = decrementedTime.getSecond()
                            + decrementedTime.getMinute() * 60
                            + decrementedTime.getHour() * 3600;
                }
            }
        });
    }

    /**
     * Initializes the Labels.
     */
    private void initLabels()
    {
        lblBigCountdown = new JLabel("00:00:00");
        lblBigCountdown.setForeground(UiProperties.UI_TEXT_COLOR);
        lblBigCountdown.setHorizontalAlignment(SwingConstants.CENTER);
        lblBigCountdown.setFont(UiProperties.UI_COUNTDOWN_FONT);
        lblBigCountdown.setBounds(10, 38, 234, 89);
        getContentPane().add(lblBigCountdown);

        lblCurrentTimeText = new JLabel("Current Time");
        lblCurrentTimeText.setForeground(UiProperties.UI_TEXT_COLOR);
        lblCurrentTimeText.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCurrentTimeText.setBounds(10, 269, 120, 20);
        getContentPane().add(lblCurrentTimeText);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        lblCurrentTimeValue = new JLabel(formatter.format(LocalTime.now()));
        lblCurrentTimeValue.setForeground(UiProperties.UI_TEXT_COLOR);
        lblCurrentTimeValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCurrentTimeValue.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCurrentTimeValue.setBounds(140, 269, 104, 20);
        getContentPane().add(lblCurrentTimeValue);

        lblCountdownText = new JLabel("Countdown");
        lblCountdownText.setForeground(UiProperties.UI_TEXT_COLOR);
        lblCountdownText.setFont(UiProperties.UI_BASIC_TEXT_FONT);
        lblCountdownText.setBounds(10, 238, 120, 20);
        getContentPane().add(lblCountdownText);

        lblActionText = new JLabel("Action Time");
        lblActionText.setForeground(UiProperties.UI_TEXT_COLOR);
        lblActionText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblActionText.setBounds(10, 300, 120, 20);
        getContentPane().add(lblActionText);

        lblActionValue = new JLabel("20:24:01");
        lblActionValue.setForeground(UiProperties.UI_TEXT_COLOR);
        lblActionValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblActionValue.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblActionValue.setBounds(140, 300, 104, 20);
        getContentPane().add(lblActionValue);
    }

    /**
     * Initializes the spinner.
     */
    private void initSpinner()
    {
        SpinnerDateModel spinnerModel = new SpinnerDateModel();

        spnTimeSelector = new JSpinner();
        spnTimeSelector.setBounds(140, 238, 104, 20);
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

        JTextField spinnerTextField = ((JSpinner.DefaultEditor) spnTimeSelector
            .getEditor()).getTextField();
        spinnerTextField.setToolTipText(
                "Enter the countdown time. Pressing 'Return' Key will start the countdown.");
        spinnerTextField.addKeyListener(new KeyListener()
        {
            @Override
            public void keyReleased(final KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    btnStart.doClick();
                }
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
            }
        });
    }

    /**
     * Loads the settings from the settings.xml file and
     * updates the components with the loaded settings.
     */
    private void loadDefaultSettings()
    {
        File settingsFile = new File(
                ApplicationProperties.SETTINGS_FILE_LOCATION);
        if (settingsFile.exists() && !settingsFile.isDirectory())
        {
            List<String[]> defaultSettings;
            try
            {
                defaultSettings = XmlSettingsReader.loadDefaultSettings();

                String actionSetting = "default";
                String incrementTime = "default";
                String countdownTime = "default";
                String audioSetting = "default";

                for (String[] setting : defaultSettings)
                {
                    if (setting[0].equals(
                            SleepTimerSettingsXmlSaveFileCreator.XML_ACTION_SETTING_TAG_NAME))
                        actionSetting = setting[1];
                    if (setting[0].equals(
                            SleepTimerSettingsXmlSaveFileCreator.XML_INCREMENT_TIME))
                        incrementTime = setting[1];
                    if (setting[0].equals(
                            SleepTimerSettingsXmlSaveFileCreator.XML_COUNTDOWN_TIME_SETTING))
                        countdownTime = setting[1];
                    if (setting[0].equals(
                            SleepTimerSettingsXmlSaveFileCreator.XML_SELECTED_AUDIO))
                        audioSetting = setting[1].toLowerCase();
                }

                if (actionSetting.equals("Shutdown"))
                {
                    cBoxSettingSelector.setSelectedIndex(0);
                }
                else if (actionSetting.equals("Sleep"))
                {
                    cBoxSettingSelector.setSelectedIndex(1);
                }
                else if (actionSetting.equals("Lock"))
                {
                    cBoxSettingSelector.setSelectedIndex(2);
                }
                else if (actionSetting.equals("Restart"))
                {
                    cBoxSettingSelector.setSelectedIndex(3);
                }
                else if (actionSetting.equals("Alarm"))
                {
                    cBoxSettingSelector.setSelectedIndex(4);
                }

                DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern(ApplicationProperties.TIME_FORMAT);
                LocalDate now = LocalDate.now();
                LocalTime incrementTimeValue = LocalTime.parse(incrementTime,
                        formatter);
                LocalTime countdownTimeValue = LocalTime.parse(countdownTime,
                        formatter);

                Instant instant = incrementTimeValue.atDate(now)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
                Date incrementTimeValueAsDate = Date.from(instant);

                instant = countdownTimeValue.atDate(now)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
                Date countdownTimeValueAsDate = Date.from(instant);

                txtFldAmount.setValue(incrementTimeValueAsDate);
                spnTimeSelector.setValue(countdownTimeValueAsDate);

                //                System.out.println(incrementTimeValue.getHour() + " " + incrementTimeValue.getMinute() + " " + incrementTimeValue.getSecond());
                //                System.out.println((incrementTimeValue.getHour() * 3600
                //                        + incrementTimeValue.getMinute() * 60
                //                        + incrementTimeValue.getSecond()) / 1200);

                double partialSeconds = ((double) (incrementTimeValue.getHour()
                        * 3600 + incrementTimeValue.getMinute() * 60
                        + incrementTimeValue.getSecond())) / 1200;
                slider.setValue((int) Math.round(partialSeconds * 100));

                if (audioSetting.equals("alarm"))
                {
                    rmnitmAlarm.setSelected(true);
                }
                else if (audioSetting.equals("noice"))
                {
                    rmnitmNoice.setSelected(true);
                }
            }
            catch (Exception e)
            {
                Dialogs.showLoadSettingsError(this);
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the Frame properties.
     */
    private void setFrameProperties()
    {
        ImageIcon appIcon = new ImageIcon(System.getProperty("user.dir")
                + File.separator + "resources" + File.separator + "icons"
                + File.separator + "moonIcon.png");
        setIconImage(appIcon.getImage());
        setVisible(true);
        setTitle(GUI_TITLE + " " + GUI_VERSION);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(UiProperties.UI_MENU_BG_COLOR);
    }

    // =============== END CONSTRUCTOR & INIT METHODS ===============

    /**
     * Sets the Gui elements according to the application state.
     * 
     * @param state The state the GUI is currently in.
     */
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

    /**
     * Starts the thread that updates the current time and the action time.
     */
    private void startTimeThread()
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

    /**
     * Shuts down threads, saves the settings and shuts down the JVM.
     */
    private void close()
    {
        saveSettings();
        currentTimeTimerTask.cancel();
        if (countDownSeconds > 0) countdownTimerTask.cancel();
        dispose();
        System.exit(0);
    }

    /**
     * Saves the user inputs to the settings.xml file.
     */
    private void saveSettings()
    {
        try
        {
            String selectedActionSetting = (String) cBoxSettingSelector
                .getSelectedItem();

            DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(ApplicationProperties.TIME_FORMAT);
            Date incrementTime = (Date) txtFldAmount.getValue();
            Instant instant = Instant.ofEpochMilli(incrementTime.getTime());
            LocalTime time = LocalDateTime
                .ofInstant(instant, ZoneId.systemDefault())
                .toLocalTime();
            String formattedIncrementTime = formatter.format(time);

            Date countdownTime = (Date) spnTimeSelector.getValue();
            instant = Instant.ofEpochMilli(countdownTime.getTime());
            time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .toLocalTime();
            String formattedCountdownTime = formatter.format(time);

            String selectedAudioSetting = null;
            if (btGrAudio.getSelection()
                .equals(rmnitmAlarm.getModel()))
            {
                selectedAudioSetting = "Alarm";
            }
            else
            {
                selectedAudioSetting = "Noice";
            }

            SleepTimerSettingsXmlSaveFileCreator creator = new SleepTimerSettingsXmlSaveFileCreator(
                    selectedActionSetting, formattedIncrementTime,
                    formattedCountdownTime, selectedAudioSetting);
            creator
                .exportToXmlFile(ApplicationProperties.SETTINGS_FILE_LOCATION);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Converts the time in the spinner to seconds.
     */
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

    /**
     * Sets the seconds variable to initial value.
     */
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

    /**
     * Starts the main countdown. The countdown runs until the time is at
     * 00:00:00.
     */
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
                        .getSelectedItem()).toString()
                            .toLowerCase());
                    try
                    {
                        saveSettings();
                        cancelCountdownTimer();
                        if (selectedSetting.equals("shutdown")
                                || selectedSetting.equals("restart")
                                || selectedSetting.equals("lock")
                                || selectedSetting.equals("sleep"))
                        {
                            machineCommandManager
                                .sendMachineCommand(selectedSetting);
                        }
                        else if (selectedSetting.equals("alarm"))
                        {
                            String audioDir = System.getProperty("user.dir")
                                    + File.separator + "resources"
                                    + File.separator + "sounds"
                                    + File.separator;
                            String selectedAudioSetting = null;
                            if (btGrAudio.getSelection()
                                .equals(rmnitmAlarm.getModel()))
                            {
                                selectedAudioSetting = "alarm";
                            }
                            else
                            {
                                selectedAudioSetting = "noice";
                            }
                            AudioInputStream audioInputStream = AudioSystem
                                .getAudioInputStream(
                                        new File(audioDir + selectedAudioSetting
                                                + ".wav").getAbsoluteFile());
                            Clip clip = AudioSystem.getClip();
                            clip.open(audioInputStream);
                            clip.start();
                        }
                    }
                    catch (IOException e)
                    {
                        Dialogs.showIoErrorDialog(gui);
                        e.printStackTrace();
                    }
                    catch (UnsupportedAudioFileException e)
                    {
                        Dialogs.showAudioFileError(gui);
                        e.printStackTrace();
                    }
                    catch (LineUnavailableException e)
                    {
                        Dialogs.showAudioFileError(gui);
                        e.printStackTrace();
                    }
                }
            }
        };
        countdownTimer = new Timer();
        countdownTimer.schedule(countdownTimerTask, 0, 1000);
    }

    /**
     * Cancels the main countdown.
     */
    private void cancelCountdownTimer()
    {
        countdownTimer.cancel();
    }
}
