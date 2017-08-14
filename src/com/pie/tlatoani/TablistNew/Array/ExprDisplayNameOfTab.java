package com.pie.tlatoani.TablistNew.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class ExprDisplayNameOfTab extends SimpleExpression<String> {
    private Expression<Number> column;
    private Expression<Number> row;
    private Expression<Player> playerExpression;

    @Override
    protected String[] get(Event event) {
        int column = this.column.getSingle(event).intValue();
        int row = this.row.getSingle(event).intValue();
        Player[] players = playerExpression.getArray(event);
        String[] displayNames = new String[players.length];
        for (int i = 0; i < players.length; i++) {
            Tablist tablist = TablistManager.getTablistOfPlayer(players[i]);
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                displayNames[i] = arrayTablist.getTab(column, row).getDisplayName();
            }
        }
        return displayNames;
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "display name of tab " + column + ", " + row + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        column = (Expression<Number>) expressions[0];
        row = (Expression<Number>) expressions[1];
        playerExpression = (Expression<Player>) expressions[2];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        int column = this.column.getSingle(event).intValue();
        int row = this.row.getSingle(event).intValue();
        String value = (String) delta[0];
        for (Player player : playerExpression.getArray(event)) {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                arrayTablist.getTab(column, row).setDisplayName(value);
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}