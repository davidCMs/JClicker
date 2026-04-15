package dev.davidCMs.jclicker.dbus.statusnotifier;

public enum Category {

    ApplicationStatus("ApplicationStatus"),
    Communications("Communications"),
    SystemServices("SystemServices"),
    Hardware("Hardware"),
    ;

    final String string;

    Category(String string) {
        this.string = string;
    }
}
