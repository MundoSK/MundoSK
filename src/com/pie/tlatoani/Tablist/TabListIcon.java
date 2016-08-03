package com.pie.tlatoani.Tablist;

import com.pie.tlatoani.Tablist.SkinTexture.SkinTexture;
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
    public final SkinTexture skinTexture;

    public static TabListIcon steve() {
        return new TabListIcon(IconType.STEVE, null, null, null);
    }

    public static TabListIcon alex() {
        return new TabListIcon(IconType.ALEX, null, null, null);
    }

    public static TabListIcon playerUUID(UUID playerUUID) {
        return new TabListIcon(IconType.PLAYER, null, playerUUID, null);
    }

    public static TabListIcon url(String url) {
        return new TabListIcon(IconType.URL, url, null, null);
    }

    public static TabListIcon skinTexture(SkinTexture skinTexture) {
        return new TabListIcon(IconType.SKINTEXTURE, null, null, skinTexture);
    }

    private TabListIcon(IconType type, String url, UUID playerUUID, SkinTexture skinTexture) {
        this.type = type;
        this.url = url;
        this.playerUUID = playerUUID;
        this.skinTexture = skinTexture;
    }

    public enum IconType {
        STEVE,
        ALEX,
        PLAYER,
        URL,
        SKINTEXTURE,
    }

    @Override
    public boolean equals(Object other) {
        TabListIcon otherIcon;
        if (other instanceof TabListIcon && type == (otherIcon = (TabListIcon) other).type) {
            if (type == IconType.STEVE || type == IconType.ALEX) return true;
            if (type == IconType.PLAYER) return playerUUID.equals((otherIcon).playerUUID);
            if (type == IconType.URL) return url.equals(otherIcon.url);
            if (type == IconType.SKINTEXTURE) return skinTexture.equals(otherIcon.skinTexture);
        }
        return false;
    }

    public Object convertTabListIconToSkriptValue() {
        switch (this.type) {
            case STEVE: return  "steve";
            case ALEX: return  "alex";
            case PLAYER: return Bukkit.getOfflinePlayer(this.playerUUID);
            case URL: return this.url;
            case SKINTEXTURE: return this.skinTexture;
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
        } else if (value instanceof SkinTexture) {
            return skinTexture((SkinTexture) value);
        } else {
            return null;
        }
    }
}
