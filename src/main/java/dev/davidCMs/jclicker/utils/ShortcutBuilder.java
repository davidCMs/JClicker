package dev.davidCMs.jclicker.utils;

import java.util.ArrayList;
import java.util.List;

public class ShortcutBuilder {
    private final List<Shortcut> shortcuts = new ArrayList<>();

    public ShortcutBuilder add(String id, String description, String preferredTrigger) {
        shortcuts.add(new Shortcut(id, description, preferredTrigger));
        return this;
    }

    public List<Shortcut> build() {
        return shortcuts;
    }
}
