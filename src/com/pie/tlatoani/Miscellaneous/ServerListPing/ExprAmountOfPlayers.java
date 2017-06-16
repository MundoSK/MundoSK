package com.pie.tlatoani.Miscellaneous.ServerListPing;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Tlatoani on 6/16/17.
 */
public class ExprAmountOfPlayers extends SimpleExpression<Number> {
    private boolean max;

    @Override
    protected Number[] get(Event event) {
        return new Number[]{max ? ((ServerListPingEvent) event).getMaxPlayers() : ((ServerListPingEvent) event).getNumPlayers()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return max ? "shown max amount of players" : "shown amount of players";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        max = parseResult.mark == 1;
        if (!ScriptLoader.isCurrentEvent(ServerListPingEvent.class)) {
            Skript.error("The '" + toString() + "' expression can only be used in a server list ping event!");
        }
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (!max) {
            throw new IllegalStateException();
        }
        int value;
        if (mode == Changer.ChangeMode.SET) {
            value = ((Number) delta[0]).intValue();
        } else if (mode == Changer.ChangeMode.RESET) {
            value = Bukkit.getServer().getMaxPlayers();
        } else {
            throw new IllegalArgumentException();
        }
        ((ServerListPingEvent) event).setMaxPlayers(value);
    }

    @Override
    public Class[] acceptChange(Changer.ChangeMode mode) {
        if (max && (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET)) return CollectionUtils.array(Number.class);
        return null;
    }
}