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
public class ExprIP extends SimpleExpression<String> {

    @Override
    protected String[] get(Event event) {
        return new String[]{((ServerListPingEvent) event).getAddress().getHostAddress()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "pinger's ip";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(ServerListPingEvent.class)) {
            Skript.error("The 'pinger's ip' expression can only be used in a server list ping event!");
        }
        return true;
    }
}