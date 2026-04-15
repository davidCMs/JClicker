package dev.davidCMs.jclicker.dbus.statusnotifier;

@FunctionalInterface
public interface PositionConsumer {
    void accept(int x, int y);
}
