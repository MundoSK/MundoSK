package com.pie.tlatoani.Miscellaneous.ArmorStand;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * Created by Tlatoani on 6/23/16.
 */
public class EvtArmorStandPlace extends SkriptEvent {
    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return event instanceof EntitySpawnEvent && ((EntitySpawnEvent) event).getEntity() instanceof ArmorStand;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "armor stand place";
    }
}
