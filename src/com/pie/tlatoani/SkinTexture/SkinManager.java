package com.pie.tlatoani.SkinTexture;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tlatoani on 9/18/16.
 */
public class SkinManager {
    private static HashMap<UUID, SkinTexture> textureMap = new HashMap<>();

    private SkinManager() {}

    public static void onJoin(Player player) {
        textureMap.put(player.getUniqueId(), new SkinTexture(player));
    }

    public static void onQuit(Player player) {
        textureMap.remove(player.getUniqueId());
    }

    public static SkinTexture getCurrentSkin(Player player) {
        return textureMap.get(player.getUniqueId());
    }

    public static void setCurrentSkin(Player player, SkinTexture skinTexture) {
        textureMap.put(player.getUniqueId(), skinTexture);
    }
}
