package dev.davidCMs.jclicker.utils;

import dev.davidCMs.jclicker.Main;
import dev.davidCMs.jclicker.dbus.DBusManager;
import dev.davidCMs.jclicker.dbus.Request;
import dev.davidCMs.jclicker.exceptions.RequestFailedException;
import dev.davidCMs.jclicker.exceptions.UserCanceledException;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

public class RemoteDesktopManager {

    private final static Logger log = LoggerFactory.getLogger(RemoteDesktopManager.class);

    private final DBusManager manager;
    private final String sessionHandle;
    private final DBusPath sessionHandlePath;

    public RemoteDesktopManager(DBusManager manager) throws InterruptedException, UserCanceledException, RequestFailedException {
        this.manager = manager;
        Request.Response createSessionResponse = manager.waitFor(
                manager.remoteDesktop.createSession(
                        Map.of(
                                "handle_token",         new Variant<>(DBusManager.genHandleToken()),
                                "session_handle_token", new Variant<>(DBusManager.genHandleToken())
                        )
                ).toString(),
                1000, TimeUnit.SECONDS
        );

        switch (createSessionResponse.code.intValue()) {
            case 0 -> log.info("CreateSession OK");
            case 1 -> throw new UserCanceledException("User canceled CreateSession");
            case 2 -> throw new RequestFailedException("CreateSession Failed");
            default -> throw new RequestFailedException("Portal went insane while CreateSession ran");
        }

        this.sessionHandle = (String) createSessionResponse.results.get("session_handle").getValue();
        this.sessionHandlePath = new DBusPath(sessionHandle);

        String token = AppPreferences.getRemoteDesktopToken();

        Map<String, Variant<?>> map = new HashMap<>();
        map.put("handle_token", new Variant<>(DBusManager.genHandleToken()));
        map.put("types",        new Variant<>(new UInt32(2)));
        map.put("persist_mode", new Variant<>(new UInt32(2)));
        if (token != null) map.put("restore_token", new Variant<>(token));

        Request.Response selectDevicesRequest = manager.waitFor(
                manager.remoteDesktop.selectDevices(
                        sessionHandlePath,
                        map
                ),
                1000, TimeUnit.SECONDS
        );

        switch (selectDevicesRequest.code.intValue()) {
            case 0 -> log.info("SelectDevices OK");
            case 1 -> throw new UserCanceledException("User canceled SelectDevices");
            case 2 -> throw new RequestFailedException("SelectDevices Failed");
            default -> throw new RequestFailedException("Portal went insane while SelectDevices ran");
        }


        Request.Response startResponse = manager.waitFor(
                manager.remoteDesktop.start(
                        sessionHandlePath,
                        "Null",
                        Map.of(
                                "handle_token", new Variant<>(DBusManager.genHandleToken())
                        )
                ),
                1000, TimeUnit.SECONDS
        );

        /*
        * JOptionPane.showMessageDialog(null,
                        "User canceled remote desktop! Please allow the remote desktop as it is essential for this application to work.",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
        * */

        switch (startResponse.code.intValue()) {
            case 0 -> log.info("Start OK");
            case 1 -> throw new UserCanceledException("User canceled Start");
            case 2 -> throw new RequestFailedException("Start Failed");
            default -> throw new RequestFailedException("Portal went insane while Start ran");
        }

        AppPreferences.setRemoteDesktopToken((String) startResponse.results.get("restore_token").getValue());

        int devices = ((UInt32) startResponse.results.get("devices").getValue()).intValue();
        if ((devices & 2) == 0) throw new RuntimeException("User canceled mouse input");
    }

    public void setMouseButton(MouseButton button, boolean pressed) {
        manager.remoteDesktop.notifyPointerButton(sessionHandlePath, button, pressed);
    }

}
