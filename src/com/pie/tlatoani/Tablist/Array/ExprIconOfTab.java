package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.TabListIcon;
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/25/16.
 */
public class ExprIconOfTab extends SimpleExpression<Object> {
    private Expression<Player> playerExpression;
    private Expression<Number> column;
    private Expression<Number> row;
    private int pattern;

    @Override
    protected Object[] get(Event event) {
        ArrayTabList arrayTabList;
        if ((arrayTabList = TabListManager.getArrayTabListForPlayer(playerExpression.getSingle(event))) != null) {
            TabListIcon tabListIcon = pattern == 0 ? arrayTabList.getHead(column.getSingle(event).intValue(), row.getSingle(event).intValue()) : arrayTabList.initialIcon;
            return new Object[]{tabListIcon.convertTabListIconToSkriptValue()};
        }
        return new Object[0];
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return pattern == 0 ? "icon of tab " + column + ", " + row + " for " + playerExpression : "initial icon of " + playerExpression + "'s array tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if ((pattern = i) == 0) {
            column = (Expression<Number>) expressions[0];
            row = (Expression<Number>) expressions[1];
            playerExpression = (Expression<Player>) expressions[2];
        } else {
            playerExpression = (Expression<Player>) expressions[0];
        }
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        ArrayTabList arrayTabList;
        if ((arrayTabList = TabListManager.getArrayTabListForPlayer(playerExpression.getSingle(event))) != null) {
            TabListIcon value = TabListIcon.convertSkriptValueToTabListIcon(delta[0]);
            if (pattern == 0) {
                arrayTabList.setHead(column.getSingle(event).intValue(), row.getSingle(event).intValue(), value);
            } else {
                arrayTabList.initialIcon = value;
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class, OfflinePlayer.class);
        }
        return null;
    }
}
