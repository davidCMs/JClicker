package dev.davidCMs.jclicker.utils;

import dev.davidCMs.jclicker.dbus.DBusManager;
import dev.davidCMs.jclicker.dbus.GlobalShortcutDef;
import dev.davidCMs.jclicker.dbus.Request;
import dev.davidCMs.jclicker.dbus.ShortcutStruct;
import dev.davidCMs.jclicker.exceptions.RequestFailedException;
import dev.davidCMs.jclicker.exceptions.UserCanceledException;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.errors.UnknownMethod;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ShortcutManager {
    private final static Logger log = LoggerFactory.getLogger(ShortcutManager.class);

    private final DBusManager dBusManager;
    private final String sessionHandle;
    private final DBusPath sessionHandlePath;

    public final Map<String, Shortcut> shortcuts = new HashMap<>();
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    public ShortcutManager(DBusManager dBusManager, List<Shortcut> shortcuts) throws InterruptedException, UserCanceledException, RequestFailedException {
        this.dBusManager = dBusManager;
        for (Shortcut s : shortcuts) {
            this.shortcuts.put(s.getId(), s);
        }

        Request.Response createSessionResponse = dBusManager.waitFor(
                dBusManager.globalShortcut.createSession(
                        Map.of(
                                "handle_token", new Variant<>(DBusManager.genHandleToken()),
                                "session_handle_token", new Variant<>(DBusManager.genHandleToken())
                        )
                ),
                30, TimeUnit.SECONDS
        );

        switch (createSessionResponse.code.intValue()) {
            case 0 -> log.info("CreateSession OK");
            case 1 -> throw new UserCanceledException("User canceled CreateSession");
            case 2 -> throw new RequestFailedException("CreateSession Failed");
            default -> throw new RequestFailedException("Portal went insane while CreateSession ran");
        }

        this.sessionHandle = (String) createSessionResponse.results.get("session_handle").getValue();
        this.sessionHandlePath = new DBusPath(sessionHandle);

        Request.Response bindShortcutsResponse = dBusManager.waitFor(
                dBusManager.globalShortcut.bindShortcuts(
                        sessionHandlePath,
                        shortcuts.stream().map(Shortcut::toStruct).toList(),
                        "Null",
                        Map.of(
                                "handle_token", new Variant<>(DBusManager.genHandleToken())
                        )
                ),
                1000, TimeUnit.DAYS
        );

        switch (bindShortcutsResponse.code.intValue()) {
            case 0 -> log.info("BindShortcuts OK");
            case 1 -> throw new UserCanceledException("User canceled BindShortcuts");
            case 2 -> throw new RequestFailedException("BindShortcuts Failed");
            default -> throw new RequestFailedException("Portal went insane while BindShortcuts ran");
        }

        List<?> returnedShortcuts;
        try {
            returnedShortcuts = (ArrayList<?>) bindShortcutsResponse.results.get("shortcuts").getValue();
        } catch (ClassCastException e) {
            throw new RuntimeException("Portal went insane: " + e.getMessage());
        }


        List<ShortcutStruct> newShortcuts = new ArrayList<>();
        for (Object o : returnedShortcuts) {
            Object[] arr = (Object[]) o;
            String id = (String) arr[0];
            @SuppressWarnings("unchecked")
            Map<String, Variant<?>> properties = (Map<String, Variant<?>>) arr[1];

            newShortcuts.add(new ShortcutStruct(id, properties));
        }

        for (ShortcutStruct shortcut : newShortcuts) {
            String triggerDescription = (String) shortcut.properties.get("trigger_description").getValue();
            log.info("\n{}\n\tDescription: {}\n\tTrigger: {}",
                    shortcut.id,
                    shortcut.properties.get("description").getValue(),
                    triggerDescription);
            this.shortcuts.get(shortcut.id).triggerDescription.set(triggerDescription);
        }

        try {
            dBusManager.conn.addSigHandler(
                    GlobalShortcutDef.Activated.class,
                    activated -> pool.submit(() -> triggerActivate(activated.id))
            );

            dBusManager.conn.addSigHandler(
                    GlobalShortcutDef.Deactivated.class,
                    deactivated -> pool.submit(() -> triggerDeactivate(deactivated.id))
            );

            dBusManager.conn.addSigHandler(
                    GlobalShortcutDef.ShortcutsChanged.class,
                    shortcutsSig -> pool.submit(() -> handleShortcutChange(shortcutsSig.shortcuts))
            );
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    private void triggerActivate(String id) {
        Shortcut s = shortcuts.get(id);
        if (!s.activated.getAndSet(true)) {
            for (Consumer<Boolean> r : s.onStateChanged) {
                r.accept(true);
            }
        } else {
            for (Consumer<Boolean> r : s.onStateChanged) {
                r.accept(false);
            }
        }

        for (Runnable r : s.onActivate) {
            r.run();
        }
    }

    private void triggerDeactivate(String id) {
        Shortcut s = shortcuts.get(id);
        if (s.activated.getAndSet(false)) {
            for (Consumer<Boolean> r : s.onStateChanged) {
                r.accept(false);
            }
        } else {
            for (Consumer<Boolean> r : s.onStateChanged) {
                r.accept(true);
            }
        }

        for (Runnable r : s.inDeactivate) {
            r.run();
        }
    }

    private void handleShortcutChange(List<ShortcutStruct> shortcutStructs) {
        for (ShortcutStruct shortcutStruct : shortcutStructs) {
            Shortcut shortcut = shortcuts.get(shortcutStruct.id);
            String newTriggerDesc = (String) shortcutStruct.properties.get("trigger_description").getValue();
            shortcut.setTriggerDescription(newTriggerDesc);
        }
    }

    public void configureShortcuts() {
        try {
            dBusManager.globalShortcut.configureShortcuts(
                    sessionHandlePath,
                    "Null",
                    Map.of()
            );
        } catch (UnknownMethod e) {
            throw new RuntimeException("Not supported");
        }
    }
}