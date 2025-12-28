package dev.davidCMs.jclicker.dbus;

import dev.davidCMs.jclicker.utils.MouseButton;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Map;

public class RemoteDesktop implements RemoteDesktopDef {
    private static final Logger log = LoggerFactory.getLogger(RemoteDesktop.class);
    private final RemoteDesktopDef remoteDesktop;
    private static final Map<String, Variant<?>> NULLMAP = Map.of();
    private static final UInt32 TRUE = new UInt32(1);
    private static final UInt32 FALSE = new UInt32(0);

    public RemoteDesktop(DBusConnection conn) {
        try {
            this.remoteDesktop = conn.getRemoteObject(
                    "org.freedesktop.portal.Desktop",
                    "/org/freedesktop/portal/desktop",
                    RemoteDesktopDef.class
            );
        } catch (DBusException e) {
            JOptionPane.showMessageDialog(
                    null,
                    """
                            Failed to get RemoteDesktop object from dbus
                            Please make share you have got XDG desktop portal installed
                            """,
                    "Cannot initialise RemoteDesktop",
                    JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException("Failed to get RemoteDesktop");
        }

        try {
            Properties properties = conn.getRemoteObject(
                    "org.freedesktop.portal.Desktop",
                    "/org/freedesktop/portal/desktop",
                    Properties.class
            );
            UInt32 version = properties.Get("org.freedesktop.portal.RemoteDesktop", "version");
            log.info("RemoteDesktop version: " + version);
            if (version.intValue() < 2) {
                JOptionPane.showMessageDialog(
                        null,
                        """
                                Your version of the RemoteDesktop is to old!
                                Please update it, if you cant go pester someone who can.
                                """,
                        "Portal out of date",
                        JOptionPane.ERROR_MESSAGE
                );
                throw new RuntimeException("Portal out of date");
            }
        } catch (DBusException e) {
            JOptionPane.showMessageDialog(
                    null,
                    """
                            Failed to get RemoteDesktop properties from dbus
                            """,
                    "Cannot initialise RemoteDesktop",
                    JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException(e);
        }
    }

    @Override
    public DBusPath createSession(Map<String, Variant<?>> options) {
        return remoteDesktop.createSession(options);
    }

    @Override
    public DBusPath selectDevices(DBusPath sessionHandle, Map<String, Variant<?>> options) {
        return remoteDesktop.selectDevices(sessionHandle, options);
    }

    @Override
    public DBusPath start(DBusPath sessionHandle, String parentWindow, Map<String, Variant<?>> options) {
        return remoteDesktop.start(sessionHandle, parentWindow, options);
    }

    @Override
    public void notifyPointerMotion(DBusPath sessionHandle, Map<String, Variant<?>> options, double dx, double dy) {
        remoteDesktop.notifyPointerMotion(sessionHandle, options, dx, dy);
    }

    public void notifyPointerMotion(DBusPath sessionHandle, double dx, double dy) {
        remoteDesktop.notifyPointerMotion(sessionHandle, NULLMAP, dx, dy);
    }

    @Override
    public void notifyPointerButton(DBusPath sessionHandle, Map<String, Variant<?>> options, int button, UInt32 state) {
        remoteDesktop.notifyPointerButton(sessionHandle, options, button, state);
    }

    public void notifyPointerButton(DBusPath sessionHandle, int button, UInt32 state) {
        remoteDesktop.notifyPointerButton(sessionHandle, NULLMAP, button, state);
    }

    public void notifyPointerButton(DBusPath sessionHandle, Map<String, Variant<?>> options, MouseButton button, boolean state) {
        remoteDesktop.notifyPointerButton(sessionHandle, options, button.getButton(), state ? TRUE : FALSE);
    }

    public void notifyPointerButton(DBusPath sessionHandle, MouseButton button, boolean state) {
        remoteDesktop.notifyPointerButton(sessionHandle, NULLMAP, button.getButton(), state ? TRUE : FALSE);
    }

    @Override
    public String getObjectPath() {
        return remoteDesktop.getObjectPath();
    }

    @Override
    public boolean isRemote() {
        return remoteDesktop.isRemote();
    }
}
