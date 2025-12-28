package dev.davidCMs.jclicker.dbus;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusMemberName;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

@DBusInterfaceName("org.freedesktop.portal.Request")
public interface Request extends DBusInterface {

    @DBusMemberName("Close")
    void close();

    @DBusMemberName("Response")
    class Response extends DBusSignal {
        public final UInt32 code;
        public final Map<String, Variant<?>> results;

        public Response(String path, UInt32 code, Map<String, Variant<?>> results) throws DBusException {
            super(path, code, results);
            this.code = code;
            this.results = results;
        }
    }


}
