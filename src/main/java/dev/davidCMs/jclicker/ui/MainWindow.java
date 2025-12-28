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

    private final Main main;

    private final ClickerThread thread;

    private final JLabel active = new JLabel("Stopped");

    private final JSpinner h;
    private final JSpinner m;
    private final JSpinner s;
    private final JSpinner ms;
    private final JSpinner ns;

    private final JComboBox<MouseButton> buttonComboBox;

    private final JPanel shortcutsPanel;

    private final JButton activate;

    public MainWindow(Main main) {
        this.main = main;
        thread = new ClickerThread(main);

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

        this.h = setupDelayField( "Hours",   AppPreferences.getDelayH(),  0, Long.MAX_VALUE, delay);
        this.m = setupDelayField( "Minutes", AppPreferences.getDelayM(),  0, Long.MAX_VALUE, delay);
        this.s = setupDelayField( "Seconds", AppPreferences.getDelayS(),  0, Long.MAX_VALUE, delay);
        this.ms = setupDelayField("Micro",   AppPreferences.getDelayMS(), 0, Long.MAX_VALUE, delay);
        this.ns = setupDelayField("Nano",    AppPreferences.getDelayNS(), 0, Long.MAX_VALUE, delay);

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
        c.weightx = 0.33;
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
        c.weightx = 0.33;
        add(clickOptions, c);

        shortcutsPanel = new JPanel();
        shortcutsPanel.setLayout(new BoxLayout(shortcutsPanel, BoxLayout.Y_AXIS));
        shortcutsPanel.setBorder(createBorder("Shortcuts", 2, 10, 10, 10, 10));

        c.gridx = 2;
        c.weightx = 0.33;
        add(shortcutsPanel, c);

        c.gridy = 2;
        c.gridx = 0;
        c.gridwidth = 2;
        this.activate = new JButton("Start");
        activate.setForeground(Color.GREEN);
        activate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 4),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        activate.addActionListener(e -> {
            if (thread.isPaused()) {
                start();
            } else {
                stop();
            }
        });
        add(activate, c);

        JPanel ui = new JPanel();
        ui.setBorder(createBorder("UI", 2, 10 ,10, 10, 10));
        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));

        Box uiScaleBox = Box.createHorizontalBox();
        JLabel uiScaleText = new JLabel("Scale ");
        uiScaleText.setAlignmentX(Component.LEFT_ALIGNMENT);
        uiScaleBox.add(uiScaleText);
        uiScaleBox.add(Box.createHorizontalStrut(2));

        JSpinner uiScaleSpinner = new JSpinner(new SpinnerNumberModel(
                AppPreferences.getUiScale(),
                1,
                3,
                0.1
        ));
        uiScaleSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        uiScaleSpinner.addChangeListener(e -> {
            float val = ((Number) uiScaleSpinner.getValue()).floatValue();
            log.info("Changed ui scale to: " + val);
            System.setProperty("flatlaf.uiScale", val + "");
            AppPreferences.setUiScale(val);
            main.recreateWindow();
        });
        uiScaleBox.add(uiScaleSpinner);
        ui.add(uiScaleBox);

        c.gridx = 2;
        c.gridwidth = 1;
        add(ui, c);
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

            long configuredNanos   = ((Number) ns.getValue()).intValue();
            long configuredMillis  = ((Number) ms.getValue()).intValue();
            long configuredSeconds = ((Number)  s.getValue()).intValue();
            long configuredMinutes = ((Number)  m.getValue()).intValue();
            long configuredHours   = ((Number)  h.getValue()).intValue();

            if (configuredNanos >= 1_000_000) {
                long ms = configuredNanos / 1_000_000;
                configuredNanos = configuredNanos % 1_000_000;
                ns.setValue(configuredNanos);
                configuredMillis += ms;
                this.ms.setValue(configuredMillis);
            }

            if (configuredMillis >= 1_000) {
                long s = configuredMillis / 1_000;
                configuredMillis = configuredMillis % 1_000;
                ms.setValue(configuredMillis);
                configuredSeconds += s;
                this.s.setValue(configuredSeconds);
            }

            if (configuredSeconds >= 60) {
                long m = configuredSeconds / 60;
                configuredSeconds = configuredSeconds % 60;
                s.setValue(configuredSeconds);
                configuredMinutes += m;
                this.m.setValue(configuredMinutes);
            }

            if (configuredMinutes >= 60) {
                long h = configuredMinutes / 60;
                configuredMinutes = configuredMinutes % 60;
                m.setValue(configuredMinutes);
                configuredHours += h;
                this.h.setValue(configuredHours);
            }

            nanos =  (int) configuredNanos;
            milis +=       configuredMillis;
            milis +=       configuredSeconds * 1000;
            milis +=       configuredMinutes * 60000;
            milis +=       configuredHours   * 3600000;


            thread.setDelay(milis, nanos);
            log.info("Setting delay to ms: {}, ns: {}", milis, nanos);
        } catch (NumberFormatException ignored) {

        }
    }

    private static JSpinner setupDelayField(String name, long init, long min, long max, JPanel panel) {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        innerPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(init, min, max, 1)
        );
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        spinner.setAlignmentY(Component.CENTER_ALIGNMENT);
        spinner.setMaximumSize(new Dimension(spinner.getPreferredSize().width, Integer.MAX_VALUE));
        JFormattedTextField tf = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        tf.setColumns(7);
        spinner.setMaximumSize(spinner.getPreferredSize());



        JLabel label = new JLabel(name);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        label.setMinimumSize(label.getPreferredSize());

        innerPanel.add(spinner);
        innerPanel.add(Box.createHorizontalStrut(5));
        innerPanel.add(label);

        innerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, innerPanel.getPreferredSize().height));

        panel.add(innerPanel);
        //panel.add(Box.createHorizontalStrut(2));
        /*
        innerPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
        spinner.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        tf.setBorder(BorderFactory.createLineBorder(Color.GREEN));
*/

        return spinner;
    }

    public void initialise() {

        if (main.shortcutManager != null) {
            for (var shortcutEntry : main.shortcutManager.shortcuts.entrySet()) {
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

        pack();

        if (main.shortcutManager != null) {
            Shortcut f6 = main.shortcutManager.shortcuts.get(Main.TOGGLE_CLICKER_SHORTCUT_ID);
            f6.addOnStateChangedListener(bool -> {
                if (bool) return;

                if (thread.isPaused()) {
                    start();
                } else {
                    stop();
                }
            });
            Shortcut f5 = main.shortcutManager.shortcuts.get(Main.AUTOCLICK_WHILE_HEALED_SHORTCUT_ID);
            f5.addOnStateChangedListener(pressed -> {
                if (pressed) {
                    start();
                } else {
                    stop();
                }
            });
        }

        setVisible(true);
    }

    private void start() {
        if (!thread.isPaused()) return;

        thread.enable();

        SwingUtilities.invokeLater(() -> {
            active.setText("Running");
            active.setForeground(Color.GREEN);

            activate.setText("Stop");
            activate.setForeground(Color.RED);
        });
    }

    private void stop() {
        if (thread.isPaused()) return;

        thread.disable();

        SwingUtilities.invokeLater(() -> {
            active.setText("Stopped");
            active.setForeground(Color.RED);

            activate.setText("Start");
            activate.setForeground(Color.GREEN);
        });
    }

}
