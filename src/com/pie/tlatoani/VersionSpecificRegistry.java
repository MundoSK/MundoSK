package com.pie.tlatoani;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Created by Tlatoani on 7/5/16.
 *
 * Used to register expressions/effects/etc. specific to Minecraft 1.9 and 1.10
 */
public class VersionSpecificRegistry {

    private VersionSpecificRegistry() {} //Cannot be initialized

    public static void register() {
        //Miscellaneous
        Skript.registerEvent("Player Swap Hands Event", SimpleEvent.class, PlayerSwapHandItemsEvent.class, "player swap hands");
    }
}
