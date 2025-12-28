package dev.davidCMs.jclicker.dbus;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusMemberName;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.UInt64;
import org.freedesktop.dbus.types.Variant;

import javax.naming.ldap.SortControl;
import java.util.List;
import java.util.Map;

@DBusInterfaceName("org.freedesktop.portal.GlobalShortcuts")
public interface GlobalShortcutDef extends DBusInterface {

    @DBusMemberName("CreateSession")
    DBusPath createSession(Map<String, Variant<?>> options);

    @DBusMemberName("BindShortcuts")
    DBusPath bindShortcuts(
            DBusPath sessionHandle,
            List<ShortcutStruct> shortcuts,
            String parentWindow,
            Map<String, Variant<?>> options
    );

    @DBusMemberName("ListShortcuts")
    DBusPath listShortcuts(DBusPath sessionHandle, Map<String, Variant<?>> options);

    @DBusMemberName("ConfigureShortcuts")
    void configureShortcuts(DBusPath sessionHandle, String parentWindow, Map<String, Variant<?>> options);

    @DBusMemberName("Activated")
    class Activated extends DBusSignal {
        public final DBusPath sessionHandle;
        public final String id;
        public final UInt64 timestamp;
        public final Map<String, Variant<?>> results;

        public Activated(String path, DBusPath sessionHandle, String id, UInt64 timestamp, Map<String, Variant<?>> results) throws DBusException {
            super(path, sessionHandle, id, timestamp, results);
            this.sessionHandle = sessionHandle;
            this.id = id;
            this.timestamp = timestamp;
            this.results = results;
        }
    }

    @DBusMemberName("Deactivated")
    class Deactivated extends DBusSignal {
        public final DBusPath sessionHandle;
        public final String id;
        public final UInt64 timestamp;
        public final Map<String, Variant<?>> results;

        public Deactivated(String path, DBusPath sessionHandle, String id, UInt64 timestamp, Map<String, Variant<?>> results) throws DBusException {
            super(path, sessionHandle, id, timestamp, results);
            this.sessionHandle = sessionHandle;
            this.id = id;
            this.timestamp = timestamp;
            this.results = results;
        }
    }

    @DBusMemberName("ShortcutsChanged")
    class ShortcutsChanged extends DBusSignal {
        public final DBusPath sessionHandle;
        public final List<ShortcutStruct> shortcuts;

        public ShortcutsChanged(String path, DBusPath sessionHandle, List<ShortcutStruct> shortcuts) throws DBusException {
            super(path, sessionHandle, sessionHandle);
            this.sessionHandle = sessionHandle;
            this.shortcuts = shortcuts;
        }
    }

}
