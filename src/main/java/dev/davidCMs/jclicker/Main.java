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

    public static final String TOGGLE_CLICKER_SHORTCUT_ID = "TOGGLE_CLICKER";
    public static final String AUTOCLICK_WHILE_HEALED_SHORTCUT_ID = "AUTOCLICK_WHILE_HEALED";

    public final DBusManager dBusManager;
    public final ShortcutManager shortcutManager;
    public final RemoteDesktopManager remoteDesktopManager;

    private Main() {
        Thread.currentThread().setName("Main");

        System.setProperty("flatlaf.uiScale", AppPreferences.getUiScale() + "");
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("flatlaf.experimental.linux.nativeDecorations", "true");
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        FlatLaf.setup(new FlatDarkLaf());

        if (!SystemInfo.isLinux) {
            JOptionPane.showMessageDialog(
                    null,
                    "This application is linux only, i have no clue how you even got it running elsewhere",
                    "WTF?",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        this.dBusManager = new DBusManager();

        remoteDesktopManager = initialiseRemoteDesktop(dBusManager);
        shortcutManager = initialiseShortcutManager(dBusManager);

        recreateWindow();

        Runtime.getRuntime().addShutdownHook(new Thread(dBusManager::close));
    }

    public MainWindow mainWindow = null;

    public void recreateWindow() {
        if (mainWindow != null) mainWindow.dispose();
        FlatLaf.setup(new FlatDarkLaf());
        this.mainWindow = new MainWindow(this);
        mainWindow.initialise();
    }

    public static void main(String[] args) {
        new Main();
    }

    private RemoteDesktopManager initialiseRemoteDesktop(DBusManager dBusManager) {
        RemoteDesktopManager manager = null;
        try {
            manager = new RemoteDesktopManager(dBusManager);
        } catch (RequestFailedException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (UserCanceledException ignored) {
            JOptionPane.showMessageDialog(
                    null,
                    """
                            Request for RemoteDesktop was canceled.
                            This application requires that permission to send mouse inputs.
                            You will be prompted again if you decline the application will exit
                            """,
                    "Declined Warning",
                    JOptionPane.WARNING_MESSAGE);
            try {
                manager = new RemoteDesktopManager(dBusManager);
            } catch (InterruptedException | RequestFailedException e) {
                throw new RuntimeException(e);
            } catch (UserCanceledException e) {
                JOptionPane.showMessageDialog(
                        null,
                        """
                                Request for RemoteDesktop was canceled for the second time.
                                Exiting!
                                """,
                        "Declined Exiting",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        return manager;
    }

    private ShortcutManager initialiseShortcutManager(DBusManager dBusManager) {
        ShortcutManager manager = null;
        try {
            manager = new ShortcutManager(dBusManager,
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
        log.info("Shortcuts available: " + (manager != null));

        return manager;
    }


}
