package dev.davidCMs.jclicker.dbus.statusnotifier;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusMemberName;
import org.freedesktop.dbus.interfaces.DBusInterface;

@DBusInterfaceName("org.kde.StatusNotifierWatcher")
public interface StatusNotifierWatcherDef extends DBusInterface {

    @DBusMemberName("RegisterStatusNotifierItem")
    void registerStatusNotifierItem(String service);

    @DBusMemberName("RegisterStatusNotifierHost")
    void registerStatusNotifierHost(String service);



}
