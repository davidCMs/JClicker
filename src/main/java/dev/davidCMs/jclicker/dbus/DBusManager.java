package dev.davidCMs.jclicker.dbus;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class DBusManager implements AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(DBusManager.class);

    public final DBusConnection conn;
    public final RemoteDesktop remoteDesktop;
    public final GlobalShortcutDef globalShortcut;

    private final ConcurrentHashMap<String, Request.Response> mapResponses = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Consumer<Request.Response>> mapHandlers = new ConcurrentHashMap<>();

    public DBusManager() {
        try {
            this.conn = DBusConnectionBuilder.forSessionBus().build();

            this.remoteDesktop = new RemoteDesktop(conn);
            this.globalShortcut = conn.getRemoteObject(
                    "org.freedesktop.portal.Desktop",
                    "/org/freedesktop/portal/desktop",
                    GlobalShortcutDef.class
            );

            conn.addSigHandler(
                    Request.Response.class,
                    response -> {
                        String path = response.getPath();
                        log.info("Received object with path: {}", path);

                        Consumer<Request.Response> handler = mapHandlers.remove(path);
                        if (handler != null) {
                            log.info("Executing handler for object with path: {}", path);
                            handler.accept(response);
                        } else {
                            log.info("No handler for object with path: {}", path);
                            mapResponses.put(path, response);
                        }
                    }
            );

        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHandler(DBusPath path, Consumer<Request.Response> consumer) {
        addHandler(path.toString(), consumer);
    }

    public void addHandler(String path, Consumer<Request.Response> consumer) {
        Request.Response response = mapResponses.remove(path);
        if (response != null) {
            log.info("Got handler for object: {} after receiving it, executing immediately", path);
            consumer.accept(response);
        } else {
            log.info("Registered handler for object: {}", path);
            mapHandlers.put(path, consumer);
        }
    }

    public Request.Response waitFor(DBusPath path, long timeout, TimeUnit unit) throws InterruptedException {
        return waitFor(path.toString(), timeout, unit);
    }

    public Request.Response waitFor(String path, long timeout, TimeUnit unit) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Request.Response> temp = new AtomicReference<>();
        addHandler(path, response -> {
            temp.set(response);
            latch.countDown();
        });
        if (!latch.await(timeout, unit)) {
            throw new RuntimeException("Timed out waiting for response: " + path);
        }
        return temp.get();
    }

    public static String genHandleToken() {
        return "jclicker_" + UUID.randomUUID().toString().replace('-', '_');
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
