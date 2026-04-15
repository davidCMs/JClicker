package dev.davidCMs.jclicker.dbus.statusnotifier;

import dev.davidCMs.jclicker.dbus.DBusManager;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractStatusNotifierItem implements StatusNotifierItemDef {

    private static final Logger log = LoggerFactory.getLogger(AbstractStatusNotifierItem.class);

    private final DBusManager dBusManager;
    private final String dBusPath;
    private final Category category;
    private final String id;
    private final String iconThemePath;
    private final String attentionMovieName;
    private final DBusPath menuPath; //todo change to a menu object when they get implemented.
    private final boolean isMenuItem;
    private String title;
    private Status status;
    private String iconName;
    private String overlayIconName;
    private String attentionIconName;
    private ToolTip toolTip;

    AbstractStatusNotifierItem(DBusManager dBusManager, String dBusPath, Category category, String id, String title, Status status, String iconThemePath, String iconName, String overlayIconName, String attentionIconName, String attentionMovieName, ToolTip toolTip, boolean isMenuItem, DBusPath menuPath) {
        this.dBusManager = dBusManager;
        this.dBusPath = dBusPath;
        this.category = category;
        this.id = id;
        this.iconThemePath = iconThemePath;
        this.attentionMovieName = attentionMovieName;
        this.menuPath = menuPath;
        this.isMenuItem = isMenuItem;
        this.title = title;
        this.status = status;
        this.iconName = iconName;
        this.overlayIconName = overlayIconName;
        this.attentionIconName = attentionIconName;
        this.toolTip = toolTip;
    }

    @Override
    public String getCategory() {
        return category.string;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getStatus() {
        return status.string;
    }

    @Override
    public String getIconThemePath() {
        return iconThemePath;
    }

    @Override
    public String getIconName() {
        return iconName;
    }

    @Override
    public String getOverlayIconName() {
        return overlayIconName;
    }

    @Override
    public String getAttentionIconName() {
        return attentionIconName;
    }

    @Override
    public String getAttentionMovieName() {
        return attentionMovieName;
    }

    @Override
    public ToolTip getToolTip() {
        return toolTip;
    }

    @Override
    public Boolean isItemMenu() {
        return isMenuItem;
    }

    @Override
    public DBusPath getMenuPath() {
        return menuPath;
    }

    public void setTitle(String title) {
        this.title = title;
        try {
            dBusManager.conn.sendMessage(new AbstractStatusNotifierItem.NewTitle(getObjectPath()));
        } catch (DBusException e) {
            log.error("Exception while sending NewTitle Signal: {}", e.getLocalizedMessage());
        }
    }

    public void setStatus(Status status) {
        this.status = status;
        try {
            dBusManager.conn.sendMessage(new AbstractStatusNotifierItem.NewStatus(getObjectPath(), status.string));
        } catch (DBusException e) {
            log.error("Exception while sending NewStatus Signal: {}", e.getLocalizedMessage());
        }
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
        try {
            dBusManager.conn.sendMessage(new AbstractStatusNotifierItem.NewIcon(getObjectPath()));
        } catch (DBusException e) {
            log.error("Exception while sending NewIcon Signal: {}", e.getLocalizedMessage());
        }
    }

    public void setOverlayIconName(String overlayIconName) {
        this.overlayIconName = overlayIconName;
        try {
            dBusManager.conn.sendMessage(new AbstractStatusNotifierItem.NewOverlayIcon(getObjectPath()));
        } catch (DBusException e) {
            log.error("Exception while sending NewOverlayIcon Signal: {}", e.getLocalizedMessage());
        }
    }

    public void setAttentionIconName(String attentionIconName) {
        this.attentionIconName = attentionIconName;
        try {
            dBusManager.conn.sendMessage(new AbstractStatusNotifierItem.NewAttentionIcon(getObjectPath()));
        } catch (DBusException e) {
            log.error("Exception while sending NewAttentionIcon Signal: {}", e.getLocalizedMessage());
        }
    }

    public void setToolTip(ToolTip toolTip) {
        this.toolTip = toolTip;
        try {
            dBusManager.conn.sendMessage(new AbstractStatusNotifierItem.NewToolTip(getObjectPath()));
        } catch (DBusException e) {
            log.error("Exception while sending NewToolTip Signal: {}", e.getLocalizedMessage());
        }
    }

    public void close() {
        dBusManager.conn.unExportObject(dBusPath);
    }

}
