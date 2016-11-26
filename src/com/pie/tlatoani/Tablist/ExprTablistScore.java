package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class ExprTablistScore extends SimpleExpression<Number> {
    private Expression<Player> object;
    private Expression<Tablist> tablistExpression;
    private Expression<Player> playerExpression;

    @Override
    protected Number[] get(Event event) {
        Tablist tablist = tablistExpression != null ? tablistExpression.getSingle(event) : Tablist.getTablistForPlayer(playerExpression.getSingle(event));
        return new Number[]{tablist.getScore(object.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        object = (Expression<Player>) expressions[0];
        tablistExpression = (Expression<Tablist>) expressions[1];
        playerExpression = (Expression<Player>) expressions[2];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Tablist tablist = tablistExpression != null ? tablistExpression.getSingle(event) : Tablist.getTablistForPlayer(playerExpression.getSingle(event));
        tablist.setScore(object.getSingle(event), ((Number) delta[0]).intValue());
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
