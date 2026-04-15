package dev.davidCMs.jclicker.dbus.statusnotifier;

public enum Status {

    Passive("Passive"),
    Active("Active"),
    NeedsAttention("NeedsAttention"),

    ;
    final String string;

    Status(String string) {
        this.string = string;
    }
}
