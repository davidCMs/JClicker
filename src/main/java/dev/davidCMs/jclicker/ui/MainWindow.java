package dev.davidCMs.jclicker.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import dev.davidCMs.jclicker.Main;
import dev.davidCMs.jclicker.utils.AppPreferences;
import dev.davidCMs.jclicker.utils.ClickerThread;
import dev.davidCMs.jclicker.utils.MouseButton;
import dev.davidCMs.jclicker.utils.Shortcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);
    private final ClickerThread thread = new ClickerThread();

    private final JLabel active = new JLabel("Stopped");

    private final JSpinner h;
    private final JSpinner m;
    private final JSpinner s;
    private final JSpinner ms;
    private final JSpinner ns;

    private final JComboBox<MouseButton> buttonComboBox;

    private final JPanel shortcutsPanel;

    public MainWindow() {
        setTitle("JClicker");
        setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        active.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 2, true),
                        BorderFactory.createEmptyBorder(2, 4, 2, 4)
                )
        );
        active.setForeground(Color.RED);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.BOTH;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 3;
        add(active, c);

        JPanel delay = new JPanel();
        delay.setBorder(createBorder("Delay", 2, 10, 10, 10, 10));

        delay.setLayout(new BoxLayout(delay, BoxLayout.Y_AXIS));

        this.h = setupDelayField( "Hours  ", AppPreferences.getDelayH(),  0, Long.MAX_VALUE, delay);
        this.m = setupDelayField( "Minutes", AppPreferences.getDelayM(),  0, Long.MAX_VALUE, delay);
        this.s = setupDelayField( "Seconds", AppPreferences.getDelayS(),  0, Long.MAX_VALUE, delay);
        this.ms = setupDelayField("Micro  ", AppPreferences.getDelayMS(), 0, Long.MAX_VALUE, delay);
        this.ns = setupDelayField("Nano   ", AppPreferences.getDelayNS(), 0, 999999,    delay); // limited by Thread.sleep()

        updateClickerDelay();

        ChangeListener onValueChanged = b -> updateClickerDelay();

        h.addChangeListener(onValueChanged);
        m.addChangeListener(onValueChanged);
        s.addChangeListener(onValueChanged);
        ms.addChangeListener(onValueChanged);
        ns.addChangeListener(onValueChanged);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveSettings();
                thread.disable();
                thread.interrupt();
            }
        });

        c.gridwidth = 1;
        c.gridy = 1;
        c.weightx = 0;
        add(delay, c);

        JPanel clickOptions = new JPanel();
        clickOptions.setBorder(createBorder("Click Options", 2, 10, 10, 10, 10));
        clickOptions.setLayout(new BoxLayout(clickOptions, BoxLayout.Y_AXIS));

        JLabel buttonComboBoxLabel = new JLabel("Mouse Button: ");
        buttonComboBoxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        clickOptions.add(buttonComboBoxLabel);

        this.buttonComboBox = new JComboBox<>(MouseButton.values());
        buttonComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonComboBox.setMaximumSize(buttonComboBox.getMinimumSize());
        buttonComboBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            MouseButton button = (MouseButton) buttonComboBox.getSelectedItem();
            log.info("Setting button to: {}", button);
            thread.setButton(button);
        });
        buttonComboBox.setSelectedIndex(AppPreferences.getMouseButtonOrdinal());
        thread.setButton((MouseButton) buttonComboBox.getSelectedItem());
        clickOptions.add(buttonComboBox);

        c.gridx = 1;
        add(clickOptions, c);

        shortcutsPanel = new JPanel();
        shortcutsPanel.setLayout(new BoxLayout(shortcutsPanel, BoxLayout.Y_AXIS));
        shortcutsPanel.setBorder(createBorder("Shortcuts", 2, 10, 10, 10, 10));

        c.gridx = 2;
        add(shortcutsPanel, c);
    }

    private Border createBorder(String title, int thickness, int top, int left, int bottom, int right) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                Color.BLACK, thickness, true
                        ), title
                ),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        );
    }

    private void saveSettings() {
        try {
            AppPreferences.setDelayH(((Number) h.getValue()).longValue());
            log.info("Saving hours delay");
        } catch (NumberFormatException e) {
            log.info("Failed to save hours delay: {}", e.getMessage());
        }
        try {
            AppPreferences.setDelayM(((Number) m.getValue()).longValue());
            log.info("Saving minutes delay");
        } catch (NumberFormatException e) {
            log.info("Failed to save minutes delay: {}", e.getMessage());
        }
        try {
            AppPreferences.setDelayS(((Number) s.getValue()).longValue());
            log.info("Saving seconds delay");
        } catch (NumberFormatException e) {
            log.info("Failed to save seconds delay: {}", e.getMessage());
        }
        try {
            AppPreferences.setDelayMS(((Number) ms.getValue()).longValue());
            log.info("Saving milliseconds delay");
        } catch (NumberFormatException e) {
            log.info("Failed to save milliseconds delay: {}", e.getMessage());
        }
        try {
            AppPreferences.setDelayNS(((Number) ns.getValue()).intValue());
            log.info("Saving nanoseconds delay");
        } catch (NumberFormatException e) {
            log.info("Failed to save nanoseconds delay: {}", e.getMessage());
        }
        log.info("Saving mouse button");
        AppPreferences.setMouseButtonOrdinal(buttonComboBox.getSelectedIndex());
    }

    private void updateClickerDelay() {
        long milis = 0;
        int nanos = 0;
        try {
            milis += ((Number) h.getValue()).longValue() * 3600000;
            milis += ((Number) m.getValue()).longValue() * 60000;
            milis += ((Number) s.getValue()).longValue() * 1000;
            milis += ((Number) ms.getValue()).longValue();

            nanos = ((Number) ns.getValue()).intValue();

            thread.setDelay(milis, nanos);
            log.info("Setting delay to ms: {}, ns: {}", milis, nanos);
        } catch (NumberFormatException ignored) {

        }
    }

    private static JSpinner setupDelayField(String name, long init, long min, long max, JPanel panel) {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(init, min, max, 1)
        );
        //spinner.setMaximumSize(new Dimension(50, 20));
        innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerPanel.add(spinner);
        innerPanel.add(new JLabel(name));
        panel.add(innerPanel);

        innerPanel.setMaximumSize(innerPanel.getPreferredSize());
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JFormattedTextField tf = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        tf.setColumns(5);

        spinner.setMaximumSize(spinner.getPreferredSize());

        return spinner;
    }

    public void initialise() {

        if (Main.shortcutManager != null) {
            for (var shortcutEntry : Main.shortcutManager.shortcuts.entrySet()) {
                final String name = shortcutEntry.getValue().getDescription();
                final Shortcut shortcut = shortcutEntry.getValue();
                final JLabel label = new JLabel(shortcut.getTriggerDescription() + " -> " + name);

                shortcut.addOnTriggerDescriptionChanged(str -> {
                    SwingUtilities.invokeLater(() -> {
                        label.setText(name + ": " + str);
                        pack();
                    });
                });

                shortcutsPanel.add(label);
            }
        } else
            shortcutsPanel.add(new JLabel("Shortcuts are unavailable ):"));

        System.setProperty("flatlaf.uiScale", "1.5");
        FlatDarkLaf.setup();
        FlatLaf.updateUI();

        pack();
        //setResizable(false);

        if (Main.shortcutManager != null) {
            Shortcut f6 = Main.shortcutManager.shortcuts.get(Main.TOGGLE_CLICKER_SHORTCUT_ID);
            f6.addOnStateChangedListener(bool -> {
                if (bool) return;

                boolean nowActive;

                if (thread.isPaused()) {
                    thread.enable();
                    nowActive = true;
                } else {
                    thread.disable();
                    nowActive = false;
                }

                SwingUtilities.invokeLater(() -> {
                    if (nowActive) {
                        active.setText("Running");
                        active.setForeground(Color.GREEN);
                    } else {
                        active.setText("Stopped");
                        active.setForeground(Color.RED);
                    }
                });

            });
            Shortcut f5 = Main.shortcutManager.shortcuts.get(Main.AUTOCLICK_WHILE_HEALED_SHORTCUT_ID);
            f5.addOnStateChangedListener(pressed -> {
                if (pressed) {
                    thread.enable();
                    active.setText("Running");
                    active.setForeground(Color.GREEN);
                } else {
                    thread.disable();
                    active.setText("Stopped");
                    active.setForeground(Color.RED);
                }
            });
        }

        setVisible(true);
    }



}
