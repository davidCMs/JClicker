package dev.davidCMs.jclicker;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;
import dev.davidCMs.jclicker.dbus.DBusManager;
import dev.davidCMs.jclicker.exceptions.RequestFailedException;
import dev.davidCMs.jclicker.exceptions.UserCanceledException;
import dev.davidCMs.jclicker.ui.MainWindow;
import dev.davidCMs.jclicker.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);


    public static final DBusManager dBusManager = new DBusManager();

    public static ShortcutManager shortcutManager = null;
    public static final String TOGGLE_CLICKER_SHORTCUT_ID = "TOGGLE_CLICKER";
    public static final String AUTOCLICK_WHILE_HEALED_SHORTCUT_ID = "AUTOCLICK_WHILE_HEALED";
    public static RemoteDesktopManager remoteDesktopManager = null;

    public static void main(String[] args) {
        Thread.currentThread().setName("Main");
        FlatLaf.setup(new FlatDarkLaf());

        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("flatlaf.experimental.linux.nativeDecorations", "true");
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        if (!SystemInfo.isLinux) {
            JOptionPane.showMessageDialog(
                    null,
                    "This application is linux only, i have no clue how you even got it running elsewhere",
                    "WTF?",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        initialiseRemoteDesktop();
        initialiseShortcutManager();

        MainWindow window = new MainWindow();
        window.initialise();

        Runtime.getRuntime().addShutdownHook(new Thread(dBusManager::close));

    }

    private static void initialiseRemoteDesktop() {
        while (Main.remoteDesktopManager == null) {
            try {
                Main.remoteDesktopManager = new RemoteDesktopManager(Main.dBusManager);
            } catch (RequestFailedException | InterruptedException e) {
                throw new RuntimeException(e);
            } catch (UserCanceledException ignored) {

            }
        }
    }

    private static void initialiseShortcutManager() {
        try {
            shortcutManager = new ShortcutManager(dBusManager,
                    new ShortcutBuilder()
                            .add(AUTOCLICK_WHILE_HEALED_SHORTCUT_ID, "Autoclicks while held", "F5")
                            .add(TOGGLE_CLICKER_SHORTCUT_ID, "Toggles autoclicker", "F6")
                            .build()
            );
        } catch (InterruptedException | RequestFailedException e) {
            throw new RuntimeException(e);
        } catch (UserCanceledException e) {
            JOptionPane.showMessageDialog(null,
                    "User canceled global shortcuts! They will not work do not report it as a bug",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
        log.info("Shortcuts available: " + (shortcutManager != null));
    }


}
