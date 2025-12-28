package dev.davidCMs.jclicker.utils;

import dev.davidCMs.jclicker.dbus.ShortcutStruct;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Shortcut extends Struct {

    private final String id;
    private final String description;
    private final String preferredTrigger;

    final AtomicReference<String> triggerDescription = new AtomicReference<>();
    final AtomicBoolean activated = new AtomicBoolean(false);

    final Set<Runnable> onActivate = new CopyOnWriteArraySet<>();
    final Set<Runnable> inDeactivate = new CopyOnWriteArraySet<>();
    final Set<Consumer<Boolean>> onStateChanged = new CopyOnWriteArraySet<>();
    final Set<Consumer<String>> onTriggerDescriptionChanged = new CopyOnWriteArraySet<>();

    public Shortcut(String id, String description, String preferredTrigger) {
        this.id = id;
        this.description = description;
        this.preferredTrigger = preferredTrigger;
    }

    public ShortcutStruct toStruct() {
        return new ShortcutStruct(id, Map.of(
                "description",          new Variant<>(description),
                "preferred_trigger",    new Variant<>(preferredTrigger)
        ));
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getPreferredTrigger() {
        return preferredTrigger;
    }

    public String getTriggerDescription() {
        return triggerDescription.get();
    }

    public void setTriggerDescription(String desc) {
        triggerDescription.set(desc);
        for (Consumer<String> consumer : onTriggerDescriptionChanged)
            consumer.accept(desc);
    }

    public boolean isActive() {
        return activated.get();
    }

    public Runnable addOnActivateListener(Runnable r) {
        onActivate.add(r);
        return r;
    }

    public Runnable addOnDeactivateListener(Runnable r) {
        inDeactivate.add(r);
        return r;
    }

    public Consumer<Boolean> addOnStateChangedListener(Consumer<Boolean> c) {
        onStateChanged.add(c);
        return c;
    }

    public Consumer<String> addOnTriggerDescriptionChanged(Consumer<String> c) {
        onTriggerDescriptionChanged.add(c);
        return c;
    }

}
