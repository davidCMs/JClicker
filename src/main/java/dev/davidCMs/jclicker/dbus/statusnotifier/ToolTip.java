package dev.davidCMs.jclicker.dbus.statusnotifier;

import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.annotations.Position;

public class ToolTip extends Struct {

    public static final ToolTip PLACEHOLDER = new ToolTip("empty", "No Title", "No Description");

    @Position(0) public final String iconName;
    @Position(1) public final IconPixmap[] iconPixmap;
    @Position(2) public final String title;
    @Position(3) public final String description;

    public ToolTip(String iconName, String title, String description) {
        this.iconName = iconName;
        this.iconPixmap = new IconPixmap[0];
        this.title = title;
        this.description = description;
    }
}
