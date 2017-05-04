package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Tlatoani on 4/20/17.
 */
public class ExprDestination extends SimpleExpression<Location> {

    @Override
    protected Location[] get(Event event) {
        if (event instanceof PlayerTeleportEvent) {
            return new Location[]{((PlayerTeleportEvent) event).getTo()};
        } else if (event instanceof EntityTeleportEvent) {
            return new Location[]{((EntityTeleportEvent) event).getTo()};
        }
        throw new IllegalArgumentException("The event " + event + " should be a PlayerTeleportEvent or EntityTeleportEvent!");
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "destination";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(PlayerTeleportEvent.class, EntityTeleportEvent.class)) {
            Skript.error("'destination' can only be used in an 'on teleport' event!");
            return false;
        }
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Location newLoc = (Location) delta[0];
        if (event instanceof PlayerTeleportEvent) {
            ((PlayerTeleportEvent) event).setTo(newLoc);
        } else if (event instanceof EntityTeleportEvent) {
            ((EntityTeleportEvent) event).setTo(newLoc);
        }
        throw new IllegalArgumentException("The event " + event + " should be a PlayerTeleportEvent or EntityTeleportEvent!");
    }
}
