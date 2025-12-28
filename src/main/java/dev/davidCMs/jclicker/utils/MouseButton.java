package dev.davidCMs.jclicker.utils;

public enum MouseButton {

    LEFT(272),
    RIGHT(273),
    MIDDLE(274),
    BACK(275),
    FORWARD(276),

    ;
    final int button;

    MouseButton(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }
}
