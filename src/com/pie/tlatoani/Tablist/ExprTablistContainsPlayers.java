package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/11/16.
 */
public class ExprTablistContainsPlayers extends SimpleExpression<Boolean> {
    private Expression<OldTablist> tablistExpression;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{!tablistExpression.getSingle(event).areAllPlayersHidden()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistExpression + " contains players";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistExpression = (Expression<OldTablist>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        OldTablist oldTablist = tablistExpression.getSingle(event);
        if ((Boolean) delta[0])
            oldTablist.showAllPlayers();
        else
            oldTablist.hideAllPlayers();
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
