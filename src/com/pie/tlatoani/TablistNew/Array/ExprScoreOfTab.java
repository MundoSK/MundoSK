package com.pie.tlatoani.TablistNew.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.TablistNew.OldTab;
import com.pie.tlatoani.TablistNew.OldTablist;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class ExprScoreOfTab extends SimpleExpression<Number> {
    private Expression<Number> column;
    private Expression<Number> row;
    private Expression<Player> playerExpression;

    @Override
    protected Number[] get(Event event) {
        int column = this.column.getSingle(event).intValue();
        int row = this.row.getSingle(event).intValue();
        Player[] players = playerExpression.getArray(event);
        Number[] scores = new Number[players.length];
        for (int i = 0; i < players.length; i++) {
            Tablist tablist = TablistManager.getTablistOfPlayer(players[i]);
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                scores[i] = arrayTablist.getTab(column, row).getScore();
            }
        }
        return scores;
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
        return "score of tab " + column + ", " + row + " for " + playerExpression;
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
        Integer value = ((Number) delta[0]).intValue();
        for (Player player : playerExpression.getArray(event)) {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                arrayTablist.getTab(column, row).setScore(value);
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