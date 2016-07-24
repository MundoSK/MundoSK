package com.pie.tlatoani.Tablist.Array;

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
 * Created by Tlatoani on 7/23/16.
 */
public class ExprSizeOfTabList extends SimpleExpression<Number> {
    private Expression<Player> playerExpression;
    private boolean isColumns;

    @Override
    protected Number[] get(Event event) {
        ArrayTabList arrayTabList;
        return new Number[] {
                (arrayTabList = TabListManager.getArrayTabListForPlayer(playerExpression.getSingle(event))) != null ?
                        (isColumns ? arrayTabList.getColumns() : arrayTabList.getRows()) :
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
        return "amount of " + (isColumns ? "column" : "row") + "s in " + playerExpression + "'s array tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        isColumns = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        ArrayTabList arrayTabList;
        if ((arrayTabList = TabListManager.getArrayTabListForPlayer(playerExpression.getSingle(event))) != null) {
            if (isColumns) {
                arrayTabList.setColumns(((Number) delta[0]).intValue());
            } else {
                arrayTabList.setRows(((Number) delta[0]).intValue());
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
