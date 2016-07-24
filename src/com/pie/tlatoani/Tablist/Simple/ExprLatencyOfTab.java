package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprLatencyOfTab extends SimpleExpression<Number> {
    private Expression<String> id;
    private Expression<Player> playerExpression;

    @Override
    protected Number[] get(Event event) {
        SimpleTabList simpleTabList;
        return new Number[] {
                (simpleTabList = TabListManager.getSimpleTabListForPlayer(playerExpression.getSingle(event))) != null ?
                        simpleTabList.getLatency(id.getSingle(event)) :
                        null
        };
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
        return "latency of tab id " + id + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        SimpleTabList simpleTabList;
        if ((simpleTabList = TabListManager.getSimpleTabListForPlayer(playerExpression.getSingle(event))) != null) {
            simpleTabList.setLatency(id.getSingle(event), ((Number) delta[0]).intValue());
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
