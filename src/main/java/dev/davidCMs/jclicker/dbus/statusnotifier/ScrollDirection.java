package dev.davidCMs.jclicker.dbus.statusnotifier;

import java.util.Map;

public enum ScrollDirection {

    Horizontal,
    Vertical

    ;
    private static final Map<String, ScrollDirection> map = Map.of(
            "Horizontal", Horizontal,
            "Vertical", Vertical
    );

    public static ScrollDirection of(String s) {
        return map.get(s);
    }

}
