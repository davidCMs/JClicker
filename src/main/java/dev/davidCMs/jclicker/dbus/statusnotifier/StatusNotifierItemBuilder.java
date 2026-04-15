package dev.davidCMs.jclicker.dbus.statusnotifier;

import dev.davidCMs.jclicker.dbus.DBusManager;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusNotifierItemBuilder {

    private static final PositionConsumer POS_NOP = (x,y) -> {};
    private static final ScrollConsumer   SCR_NOP = (delta,scrollDirection) -> {};
    private static final Logger log = LoggerFactory.getLogger(StatusNotifierItemBuilder.class);
    private static int nextId = 0;

    private Category category;
    private String id;
    private String title;
    private Status status;
    private String iconThemePath;
    private String iconName;
    private String overlayIconName;
    private String attentionIconName;
    private String attentionMovieName;
    private ToolTip toolTip;
    private boolean isMenuItem;
    private DBusPath menuPath;
    private PositionConsumer onContextMenu;
    private PositionConsumer onActivate;
    private PositionConsumer onSecondaryActivate;
    private ScrollConsumer onScroll;

    public AbstractStatusNotifierItem build(DBusManager dBusManager) {
        if (category == null) category = Category.ApplicationStatus;
        if (id == null) throw new IllegalStateException("id not set");
        if (title == null) title = id;
        if (status == null) status = Status.Passive;
        if (iconName == null) log.warn("No iconName set");
        if (isMenuItem && menuPath == null) log.error("isMenuPath is true but no menu path is set");

        PositionConsumer context = (onContextMenu != null) ? onContextMenu : POS_NOP;
        PositionConsumer activate = (onActivate != null) ? onActivate : POS_NOP;
        PositionConsumer secondary = (onSecondaryActivate != null) ? onSecondaryActivate : POS_NOP;
        ScrollConsumer scroll = (onScroll != null) ? onScroll : SCR_NOP;



        int dbid = nextId++;
        String path = "/StatusNotifierItem/" + dbid;

        AbstractStatusNotifierItem statusNotifierItem = new AbstractStatusNotifierItem(
                                              dBusManager, path,
                                              category, id, title,
                                              status, iconThemePath,
                                              iconName, overlayIconName,
                                              attentionIconName,
                                              attentionMovieName, toolTip,
                                              isMenuItem, menuPath) {
            @Override
            public void onContextMenu(int x, int y) {
                context.accept(x, y);
            }

            @Override
            public void onActivate(int x, int y) {
                activate.accept(x, y);
            }

            @Override
            public void onSecondaryActivate(int x, int y) {
                secondary.accept(x, y);
            }

            @Override
            public void onScroll(int delta, String orientation) {
                scroll.accept(delta, ScrollDirection.of(orientation));
            }

            @Override
            public String getObjectPath() {
                return path;
            }
        };

        try {
            dBusManager.conn.exportObject(path, statusNotifierItem);
            dBusManager.statusNotifierWatcher.registerStatusNotifierItem(path);
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }

        return statusNotifierItem;
    }

    public StatusNotifierItemBuilder setCategory(Category category) {
        this.category = category;
        return this;
    }

    public StatusNotifierItemBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public StatusNotifierItemBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public StatusNotifierItemBuilder setStatus(Status status) {
        this.status = status;
        return this;
    }

    public StatusNotifierItemBuilder setIconThemePath(String iconThemePath) {
        this.iconThemePath = iconThemePath;
        return this;
    }

    public StatusNotifierItemBuilder setIconName(String iconName) {
        this.iconName = iconName;
        return this;
    }

    public StatusNotifierItemBuilder setOverlayIconName(String overlayIconName) {
        this.overlayIconName = overlayIconName;
        return this;
    }

    public StatusNotifierItemBuilder setAttentionIconName(String attentionIconName) {
        this.attentionIconName = attentionIconName;
        return this;
    }

    public StatusNotifierItemBuilder setAttentionMovieName(String attentionMovieName) {
        this.attentionMovieName = attentionMovieName;
        return this;
    }

    public StatusNotifierItemBuilder setToolTip(ToolTip toolTip) {
        this.toolTip = toolTip;
        return this;
    }

    public StatusNotifierItemBuilder setMenuItem(boolean menuItem) {
        isMenuItem = menuItem;
        return this;
    }

    public StatusNotifierItemBuilder setMenuPath(DBusPath menuPath) {
        this.menuPath = menuPath;
        return this;
    }

    public StatusNotifierItemBuilder setOnContextMenu(PositionConsumer onContextMenu) {
        this.onContextMenu = onContextMenu;
        return this;
    }

    public StatusNotifierItemBuilder setOnActivate(PositionConsumer onActivate) {
        this.onActivate = onActivate;
        return this;
    }

    public StatusNotifierItemBuilder setOnSecondaryActivate(PositionConsumer onSecondaryActivate) {
        this.onSecondaryActivate = onSecondaryActivate;
        return this;
    }

    public StatusNotifierItemBuilder setOnScroll(ScrollConsumer onScroll) {
        this.onScroll = onScroll;
        return this;
    }
}
