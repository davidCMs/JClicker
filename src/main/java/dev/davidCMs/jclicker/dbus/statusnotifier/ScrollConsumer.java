package dev.davidCMs.jclicker.dbus.statusnotifier;

@FunctionalInterface
public interface ScrollConsumer {
    void accept(int delta, ScrollDirection direction);
}
