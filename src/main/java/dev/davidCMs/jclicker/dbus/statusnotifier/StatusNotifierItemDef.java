package dev.davidCMs.jclicker.dbus.statusnotifier;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.annotations.*;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.Variant;

import java.util.HashMap;
import java.util.Map;

@DBusInterfaceName("org.kde.StatusNotifierItem")
@DBusProperty(name = "Category",            access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "Id",                  access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "Title",               access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "Status",              access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "IconThemePath",       access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "IconName",            access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "OverlayIconName",     access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "AttentionIconName",   access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "AttentionMovieName",  access = DBusProperty.Access.READ, type = String.class)
@DBusProperty(name = "ToolTip",             access = DBusProperty.Access.READ, type = ToolTip.class)
@DBusProperty(name = "Menu",                access = DBusProperty.Access.READ, type = DBusPath.class)
@DBusProperty(name = "ItemIsMenu",          access = DBusProperty.Access.READ, type = Boolean.class)
public interface StatusNotifierItemDef extends DBusInterface, Properties {

    String getCategory();
    String getId();
    String getTitle();
    String getStatus();
    String getIconThemePath();
    String getIconName();
    String getOverlayIconName();
    String getAttentionIconName();
    String getAttentionMovieName();
    ToolTip getToolTip();
    DBusPath getMenuPath();
    Boolean isItemMenu();

    @Override
    @SuppressWarnings("unchecked")
    default <A> A Get(String _interfaceName, String _propertyName) {
        return switch (_propertyName) {
            case "Category"             -> (A) getCategory();
            case "Id"                   -> (A) getId();
            case "Title"                -> (A) getTitle();
            case "Status"               -> (A) getStatus();
            case "IconThemePath"        -> (A) getIconThemePath();
            case "IconName"             -> (A) getIconName();
            case "OverlayIconName"      -> (A) getOverlayIconName();
            case "AttentionIconName"    -> (A) getAttentionIconName();
            case "AttentionMovieName"   -> (A) getAttentionMovieName();
            case "ToolTip"              -> (A) getToolTip();
            case "Menu"                 -> (A) getMenuPath();
            case "ItemIsMenu"           -> (A) isItemMenu();
            default -> (A) null;
        };
    }

    private static void addIfNotNull(Map<String, Variant<?>> map, String name, String val) {
        if (val == null) val = "";
        map.put(name, new Variant<>(val));
    }

    @Override
    default Map<String, Variant<?>> GetAll(String _interfaceName) {
        Map<String, Variant<?>> map = new HashMap<>();

        addIfNotNull(map, "Category",             getCategory());
        addIfNotNull(map, "Id",                   getId());
        addIfNotNull(map, "Title",                getTitle());
        addIfNotNull(map, "Status",               getStatus());
        addIfNotNull(map, "IconThemePath",        getIconThemePath());
        addIfNotNull(map, "IconName",             getIconName());
        addIfNotNull(map, "OverlayIconName",      getOverlayIconName());
        addIfNotNull(map, "AttentionIconName",    getAttentionIconName());
        addIfNotNull(map, "AttentionMovieName",   getAttentionMovieName());

        ToolTip toolTip = getToolTip();
        if (toolTip == null) toolTip = ToolTip.PLACEHOLDER;
        map.put("ToolTip", new Variant<>(toolTip));

        DBusPath menu = getMenuPath();
        if (menu != null) map.put("Menu", new Variant<>(menu));

        Boolean isItemMenu = isItemMenu();
        if (isItemMenu == null) isItemMenu = false;

        map.put("ItemIsMenu", new Variant<>(isItemMenu));
        return map;
    }

    @Override
    default <A> void Set(String _interfaceName, String _propertyName, A _value) {}

    @DBusMemberName("ContextMenu") void onContextMenu(int x, int y);
    @DBusMemberName("Activate") void onActivate(int x, int y);
    @DBusMemberName("SecondaryActivate") void onSecondaryActivate(int x, int y);
    @DBusMemberName("Scroll") void onScroll(int delta, String orientation);

    class NewTitle extends DBusSignal {
        public NewTitle(String path) throws DBusException {
            super(path);
        }
    }

    class NewIcon extends DBusSignal {
        public NewIcon(String path) throws DBusException {
            super(path);
        }
    }

    class NewOverlayIcon extends DBusSignal {
        public NewOverlayIcon(String path) throws DBusException {
            super(path);
        }
    }

    class NewAttentionIcon extends DBusSignal {
        public NewAttentionIcon(String path) throws DBusException {
            super(path);
        }
    }

    class NewToolTip extends DBusSignal {
        public NewToolTip(String path) throws DBusException {
            super(path);
        }
    }

    class NewStatus extends DBusSignal {
        private final String status;

        public NewStatus(String path, String status) throws DBusException {
            super(path, status);
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    @Override
    default String getObjectPath() {
        return "/StatusNotifierItem";
    }
}

