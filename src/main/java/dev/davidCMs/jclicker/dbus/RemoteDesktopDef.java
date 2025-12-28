package dev.davidCMs.jclicker.dbus;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusMemberName;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

@DBusInterfaceName("org.freedesktop.portal.RemoteDesktop")
public interface RemoteDesktopDef extends DBusInterface {

    @DBusMemberName("CreateSession")
    DBusPath createSession(Map<String, Variant<?>> options);

    @DBusMemberName("SelectDevices")
    DBusPath selectDevices(DBusPath sessionHandle, Map<String, Variant<?>> options);

    @DBusMemberName("Start")
    DBusPath start(DBusPath sessionHandle, String parentWindow, Map<String, Variant<?>> options);

    @DBusMemberName("NotifyPointerMotion")
    void notifyPointerMotion(DBusPath sessionHandle, Map<String, Variant<?>> options, double dx, double dy);

    @DBusMemberName("NotifyPointerButton")
    void notifyPointerButton(DBusPath sessionHandle, Map<String, Variant<?>> options, int button, UInt32 state);
}
