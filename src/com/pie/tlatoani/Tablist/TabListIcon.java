package com.pie.tlatoani.Tablist;

import org.bukkit.entity.Player;

/**
 * Created by Tlatoani on 7/24/16.
 */
public final class TabListIcon {
    public final IconType type;
    public final String url;
    public final Player player;

    public static TabListIcon steve() {
        return new TabListIcon(IconType.STEVE, null, null);
    }

    public static TabListIcon alex() {
        return new TabListIcon(IconType.ALEX, null, null);
    }

    public static TabListIcon player(Player player) {
        return new TabListIcon(IconType.PLAYER, null, player);
    }

    public static TabListIcon url(String url) {
        return new TabListIcon(IconType.URL, url, null);
    }

    private TabListIcon(IconType type, String url, Player player) {
        this.type = type;
        if (type == IconType.STEVE || type == IconType.ALEX) {
            this.url = null;
            this.player = null;
        } else if (type == IconType.PLAYER) {
            this.url = null;
            this.player = player;
        } else {
            this.url = url;
            this.player = null;
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
            if (type == IconType.PLAYER) return player.equals((otherIcon).player);
            if (type == IconType.URL) return url.equals(otherIcon.url);
        }
        return false;
    }

    public Object convertTabListIconToSkriptValue() {
        switch (this.type) {
            case STEVE: return  "steve";
            case ALEX: return  "alex";
            case PLAYER: return this.player;
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
        } else if (value instanceof Player) {
            return player((Player) value);
        } else {
            return null;
        }
    }
}
