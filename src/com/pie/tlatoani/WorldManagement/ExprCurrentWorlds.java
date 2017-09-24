package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Tlatoani on 7/2/16.
 */
public class ExprCurrentWorlds extends SimpleExpression<World> {
    @Override
    protected World[] get(Event event) {
        List<World> worldList = Bukkit.getWorlds();
        return worldList.toArray(new World[0]);
    }

    @Override
    public Iterator<World> iterator(Event event) {
        List<World> worldList = Bukkit.getWorlds();
        return worldList.iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends World> getReturnType() {
        return World.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "current worlds";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
