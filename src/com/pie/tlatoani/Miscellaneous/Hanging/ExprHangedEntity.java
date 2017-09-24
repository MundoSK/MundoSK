package com.pie.tlatoani.Miscellaneous.Hanging;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

/**
 * Created by Tlatoani on 5/27/16.
 */
public class ExprHangedEntity extends SimpleExpression<Entity> {

    @Override
    protected Entity[] get(Event event) {
        return new Entity[]{((HangingEvent) event).getEntity()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "hanged entity";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (ScriptLoader.isCurrentEvent(HangingPlaceEvent.class) || ScriptLoader.isCurrentEvent(HangingBreakEvent.class)) {
            return true;
        }
        Skript.error("'hanged entity' can only be used in hang and unhang events!");
        return false;
    }
}
