package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Collections;

/**
 * Created by Tlatoani on 1/17/17.
 */
public class ExprTablist extends SimpleExpression<Tablist> {
    Expression<Player> playerExpression;

    @Override
    protected Tablist[] get(Event event) {
        return new Tablist[]{Tablist.getTablistForPlayer(playerExpression.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Tablist> getReturnType() {
        return Tablist.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return playerExpression + "'s tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Tablist.setTablistOfPlayer(playerExpression.getSingle(event), (Tablist) delta[0]);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Tablist.class);
        }
        return null;
    }
}
