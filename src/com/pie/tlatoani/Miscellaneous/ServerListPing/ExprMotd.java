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
public class ExprMotd extends SimpleExpression<String> {

    @Override
    protected String[] get(Event event) {
        return new String[]{((ServerListPingEvent) event).getMotd()};
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
        return "shown motd";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(ServerListPingEvent.class)) {
            Skript.error("The 'shown motd' expression can only be used in a server list ping event!");
        }
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String value;
        if (mode == Changer.ChangeMode.SET) {
            value = (String) delta[0];
        } else if (mode == Changer.ChangeMode.RESET) {
            value = Bukkit.getServer().getMotd();
        } else {
            throw new IllegalArgumentException();
        }
        ((ServerListPingEvent) event).setMotd(value);
    }

    @Override
    public Class[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(String.class);
        return null;
    }
}
