package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

/**
 * Created by Tlatoani on 7/23/16.
 */
public class ExprHeadOfTab extends SimpleExpression<OfflinePlayer> {
    private Expression<Number> column;
    private Expression<Number> row;
    private Expression<Player> playerExpression;

    @Override
    protected OfflinePlayer[] get(Event event) {
        ArrayTabList arrayTabList;
        UUID headUUID;
        return new OfflinePlayer[] {
                (arrayTabList = TabListManager.getArrayTabListForPlayer(playerExpression.getSingle(event))) != null
                        && (headUUID = arrayTabList.getHead(column.getSingle(event).intValue(), row.getSingle(event).intValue())) != null ?
                        Bukkit.getOfflinePlayer(headUUID) :
                        null
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends OfflinePlayer> getReturnType() {
        return OfflinePlayer.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "latency of tab " + column + ", " + row + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        column = (Expression<Number>) expressions[0];
        row = (Expression<Number>) expressions[1];
        playerExpression = (Expression<Player>) expressions[2];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        ArrayTabList arrayTabList;
        if ((arrayTabList = TabListManager.getArrayTabListForPlayer(playerExpression.getSingle(event))) != null) {
            if (mode == Changer.ChangeMode.SET) {
                arrayTabList.setHead(column.getSingle(event).intValue(), row.getSingle(event).intValue(), ((OfflinePlayer) delta[0]).getUniqueId());
            } else {
                arrayTabList.setHead(column.getSingle(event).intValue(), row.getSingle(event).intValue(), null);
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(OfflinePlayer.class);
        }
        return null;
    }
}
