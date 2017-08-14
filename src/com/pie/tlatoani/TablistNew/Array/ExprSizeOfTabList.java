package com.pie.tlatoani.TablistNew.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.TablistNew.Simple.SimpleTablist;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
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
        Player[] players = playerExpression.getArray(event);
        Number[] amounts = new Number[players.length];
        for (int i = 0; i < players.length; i++) {
            Tablist tablist = TablistManager.getTablistOfPlayer(players[i]);
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                amounts[i] = isColumns ? arrayTablist.getColumns() : arrayTablist.getRows();
            }
        }
        return amounts;
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
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
        int value = ((Number) delta[0]).intValue();
        for (Player player : playerExpression.getArray(event)) {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                if (isColumns) {
                    if (value > 0) {
                        arrayTablist.setColumns(value);
                    } else {
                        tablist.setSupplementaryTablist(playerTablist -> new SimpleTablist(playerTablist));
                    }
                } else {
                    arrayTablist.setRows(value);
                }
            } else if (isColumns && value > 0) {
                tablist.setSupplementaryTablist(playerTablist -> new ArrayTablist(playerTablist, value, 20, Tablist.DEFAULT_SKIN_TEXTURE));
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
