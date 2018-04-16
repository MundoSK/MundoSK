package com.pie.tlatoani.Tablist.Group;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.util.coll.iterator.EmptyIterator;
import com.pie.tlatoani.Registration.DocumentationBuilder;
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

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String name = nameExpression.getSingle(event);
        if (name == null) {
            return;
        }
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            TablistGroup group = TablistManager.getTablistGroup(name);
            for (Object object : delta) {
                Player player = (Player) object;
                if (player.isOnline()) {
                    if (mode == Changer.ChangeMode.ADD) {
                        group.add(player);
                    } else {
                        group.remove(player);
                    }
                }
            }
        } else if (mode == Changer.ChangeMode.DELETE) {
            TablistManager.deleteTablistGroup(name);
        } else {
            throw new IllegalArgumentException("Illegal ChangeMode: " + mode);
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.DELETE) {
            return CollectionUtils.array(Player[].class);
        }
        return null;
    }
}
