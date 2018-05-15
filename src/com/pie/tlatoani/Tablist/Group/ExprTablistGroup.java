package com.pie.tlatoani.Tablist.Group;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.EmptyIterator;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Iterator;

/**
 * Created by Tlatoani on 4/1/18.
 */
public class ExprTablistGroup extends SimpleExpression<Player> {
    private Expression<String> nameExpression;

    @Override
    protected Player[] get(Event event) {
        String name = nameExpression.getSingle(event);
        if (name == null) {
            return new Player[0];
        } else {
            return TablistManager.getTablistGroup(name).getPlayers().toArray(new Player[0]);
        }
    }

    @Override
    public Iterator<Player> iterator(Event event) {
        String name = nameExpression.getSingle(event);
        if (name == null) {
            return new EmptyIterator<>();
        } else {
            return TablistManager.getTablistGroup(name).getPlayers().iterator();
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Player> getReturnType() {
        return Player.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "members of tablist group " + nameExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        nameExpression = (Expression<String>) expressions[0];
        return true;
    }
}
