package dev.davidCMs.jclicker.dbus;

import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.annotations.Position;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public class ShortcutStruct extends Struct {
    @Position(0)
    public final String id;

    @Position(1)
    public final Map<String, Variant<?>> properties;

    public ShortcutStruct(String id, Map<String, Variant<?>> properties) {
        this.id = id;
        this.properties = properties;
    }
}
