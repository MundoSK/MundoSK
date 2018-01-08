package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created by Tlatoani on 3/25/17.
 */
public class ExprRespawnLocation extends SimpleExpression<Location> {

    @Override
    protected Location[] get(Event event) {
        return new Location[]{((PlayerRespawnEvent) event).getRespawnLocation()};
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
        return "respawn location";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(PlayerRespawnEvent.class)) {
            Skript.error("The 'respawn location' expression can only be used in the 'on respawn' event");
            return false;
        }
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        ((PlayerRespawnEvent) event).setRespawnLocation((Location) delta[0]);
    }

    @Override
    public Class[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Location.class);
        return null;
    }
}
