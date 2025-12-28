package dev.davidCMs.jclicker.utils;

import java.util.prefs.Preferences;

public class AppPreferences {
    private static final Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);

    private static final String remoteDesktopTokenKey = "REMOTE_DESKTOP_RESTORE_TOKEN";

    private static final String delayHKey = "DELAY_H";
    private static final String delayMKey = "DELAY_M";
    private static final String delaySKey = "DELAY_S";
    private static final String delayMSKey = "DELAY_MS";
    private static final String delayNSKey = "DELAY_NS";

    private static final String mouseButtonOrdinalKey = "MOUSE_BUTTON_ORDINAL";

    public static void setRemoteDesktopToken(String token) {
        prefs.put(remoteDesktopTokenKey, token);
    }

    public static String getRemoteDesktopToken() {
        return prefs.get(remoteDesktopTokenKey, null);
    }


    public static void setDelayH(long delay) {
        prefs.putLong(delayHKey, delay);
    }

    public static void setDelayM(long delay) {
        prefs.putLong(delayMKey, delay);
    }

    public static void setDelayS(long delay) {
        prefs.putLong(delaySKey, delay);
    }

    public static void setDelayMS(long delay) {
        prefs.putLong(delayMSKey, delay);
    }

    public static void setDelayNS(int delay) {
        prefs.putInt(delayNSKey, delay);
    }

    public static long getDelayH() {
        return prefs.getLong(delayHKey, 0);
    }

    public static long getDelayM() {
        return prefs.getLong(delayMKey, 0);
    }

    public static long getDelayS() {
        return prefs.getLong(delaySKey, 1);
    }

    public static long getDelayMS() {
        return prefs.getLong(delayMSKey, 0);
    }

    public static int getDelayNS() {
        return prefs.getInt(delayNSKey, 0);
    }

    public static void setMouseButtonOrdinal(int ordinal) {
        prefs.putInt(mouseButtonOrdinalKey, ordinal);
    }

    public static int getMouseButtonOrdinal() {
        return prefs.getInt(mouseButtonOrdinalKey, 0);
    }
}
