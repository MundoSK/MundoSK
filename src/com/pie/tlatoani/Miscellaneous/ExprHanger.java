package com.pie.tlatoani.Miscellaneous;

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
import org.bukkit.event.hanging.HangingPlaceEvent;

/**
 * Created by Tlatoani on 5/27/16.
 */
public class ExprHanger extends SimpleExpression<Entity> {
    Class<? extends Entity> returnType;

    @Override
    protected Entity[] get(Event event) {
        Entity result = null;
        if (event instanceof HangingPlaceEvent) {
            result = ((HangingPlaceEvent) event).getPlayer();
        } else if (event instanceof HangingBreakByEntityEvent) {
            result = ((HangingBreakByEntityEvent) event).getRemover();
        }
        return new Entity[]{result};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return returnType;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "hanger";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (ScriptLoader.isCurrentEvent(HangingPlaceEvent.class)) {
            returnType = Player.class;
            return true;
        }
        if (ScriptLoader.isCurrentEvent(HangingBreakEvent.class)) {
            returnType = Entity.class;
            return true;
        }
        Skript.error("'hanger' can only be used in hang and unhang events!");
        return false;
    }
}
