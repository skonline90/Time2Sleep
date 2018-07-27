package de.skonline90.Time2Sleep.view;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

public final class Gui extends JFrame
{
    private static final long serialVersionUID = 1L;

    private static final String GUI_TITLE = "Time2Sleep";
    private static final String GUI_VERSION = "0.0.1";

    private JComboBox<String> cBoxSettingSelector;

    private JLabel lblBigCountdown;
    private JLabel lblCurrentTimeText;
    private JLabel lblCurrentTimeValue;
    private JLabel lblCountdownText;

    private JButton btnAbort;
    private JButton btnStart;

    private JSpinner spnTimeSelector;

    public Gui()
    {
        setLayout();
        initMenu();
        initComboBox();
        initLabels();
        initButtons();
        addActionListeners();
        initSpinner();
        setFrameProperties();
    }

    private void setLayout()
    {
        getContentPane().setLayout(null);
        setBounds(new Rectangle(0, 0, 267, 330));
    }

    private void initMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmQuit = new JMenuItem("Quit");
        mnFile.add(mntmQuit);

        JMenu mnLanguage = new JMenu("Language");
        menuBar.add(mnLanguage);

    }

    private void initComboBox()
    {
        cBoxSettingSelector = new JComboBox<String>();
        cBoxSettingSelector.setBounds(10, 11, 234, 20);
        getContentPane().add(cBoxSettingSelector);
    }

    private void initButtons()
    {
        btnAbort = new JButton("Abort");
        btnAbort.setBounds(84, 239, 76, 23);
        getContentPane().add(btnAbort);

        btnStart = new JButton("Start");
        btnStart.setBounds(168, 239, 76, 23);
        getContentPane().add(btnStart);
    }

    private void addActionListeners()
    {
        btnAbort.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

            }
        });

        btnStart.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

            }
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

        lblCurrentTimeValue = new JLabel("23:22:11");
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
        spnTimeSelector = new JSpinner();
        spnTimeSelector.setBounds(140, 169, 104, 20);
        getContentPane().add(spnTimeSelector);
    }

    private void setFrameProperties()
    {
        setVisible(true);
        setTitle(GUI_TITLE + " " + GUI_VERSION);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
