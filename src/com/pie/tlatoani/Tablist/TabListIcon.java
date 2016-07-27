package com.pie.tlatoani.Tablist;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Created by Tlatoani on 7/24/16.
 */
public final class TabListIcon {
    public final IconType type;
    public final String url;
    public final UUID playerUUID;

    public static TabListIcon steve() {
        return new TabListIcon(IconType.STEVE, null, null);
    }

    public static TabListIcon alex() {
        return new TabListIcon(IconType.ALEX, null, null);
    }

    public static TabListIcon playerUUID(UUID playerUUID) {
        return new TabListIcon(IconType.PLAYER, null, playerUUID);
    }

    public static TabListIcon url(String url) {
        return new TabListIcon(IconType.URL, url, null);
    }

    private TabListIcon(IconType type, String url, UUID playerUUID) {
        this.type = type;
        if (type == IconType.STEVE || type == IconType.ALEX) {
            this.url = null;
            this.playerUUID = null;
        } else if (type == IconType.PLAYER) {
            this.url = null;
            this.playerUUID = playerUUID;
        } else {
            this.url = url;
            this.playerUUID = null;
        }
    }

    public enum IconType {
        STEVE,
        ALEX,
        PLAYER,
        URL
    }

    @Override
    public boolean equals(Object other) {
        TabListIcon otherIcon;
        if (other instanceof TabListIcon && type == (otherIcon = (TabListIcon) other).type) {
            if (type == IconType.STEVE || type == IconType.ALEX) return true;
            if (type == IconType.PLAYER) return playerUUID.equals((otherIcon).playerUUID);
            if (type == IconType.URL) return url.equals(otherIcon.url);
        }
        return false;
    }

    public Object convertTabListIconToSkriptValue() {
        switch (this.type) {
            case STEVE: return  "steve";
            case ALEX: return  "alex";
            case PLAYER: return Bukkit.getOfflinePlayer(this.playerUUID);
            case URL: return this.url;
            default: return null;
        }
    }

    public static TabListIcon convertSkriptValueToTabListIcon(Object value) {
        if (value instanceof String) {
            String lowerCase = ((String) value).toLowerCase();
            if (lowerCase.equals("alex")/* || lowerCase.equals("default")*/) return alex();
            else if (lowerCase.equals("steve")) return steve();
            else return url((String) value);
        } else if (value instanceof OfflinePlayer) {
            return playerUUID(((OfflinePlayer) value).getUniqueId());
        } else {
            return null;
        }
    }
}
