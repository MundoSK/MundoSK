package com.pie.tlatoani;

import ch.njol.skript.lang.util.SimpleEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Created by Tlatoani on 7/5/16.
 *
 * Used to register expressions/effects/etc. specific to Minecraft 1.9+
 */
public class RegistryFromCombatUpdate {

    private RegistryFromCombatUpdate() {} //Cannot be initialized

    public static void register() {
        //Miscellaneous
        Mundo.registerEvent("Player Swap Hands Event", SimpleEvent.class, PlayerSwapHandItemsEvent.class, "player swap hands");
    }
}
