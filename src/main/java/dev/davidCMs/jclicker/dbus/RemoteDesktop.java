package dev.davidCMs.jclicker.dbus;

import dev.davidCMs.jclicker.utils.MouseButton;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public class RemoteDesktop implements RemoteDesktopDef {
    private final RemoteDesktopDef remoteDesktop;
    private static final Map<String, Variant<?>> NULLMAP = Map.of();
    private static final UInt32 TRUE = new UInt32(1);
    private static final UInt32 FALSE = new UInt32(0);

    public RemoteDesktop(DBusConnection conn) throws DBusException {
        this.remoteDesktop = conn.getRemoteObject(
                "org.freedesktop.portal.Desktop",
                "/org/freedesktop/portal/desktop",
                RemoteDesktopDef.class
        );
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
