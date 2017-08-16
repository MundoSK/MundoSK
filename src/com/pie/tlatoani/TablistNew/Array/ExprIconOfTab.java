package com.pie.tlatoani.TablistNew.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 7/25/16.
 */
public class ExprIconOfTab extends SimpleExpression<Skin> {
    private Expression<Player> playerExpression;
    private Expression<Number> column;
    private Expression<Number> row;
    private boolean tabSpecific;

    @Override
    protected Skin[] get(Event event) {
        if (tabSpecific) {
            int column = this.column.getSingle(event).intValue();
            int row = this.row.getSingle(event).intValue();
            return Arrays
                    .stream(playerExpression.getArray(event))
                    .map(player -> {
                        Tablist tablist = TablistManager.getTablistOfPlayer(player);
                        if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                            ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                            return arrayTablist.getTab(column, row).getIcon();
                        }
                        return null;
                    })
                    .toArray(Skin[]::new);
        } else {
            return Arrays
                    .stream(playerExpression.getArray(event))
                    .map(player -> {
                        Tablist tablist = TablistManager.getTablistOfPlayer(player);
                        if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                            ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                            return arrayTablist.initialIcon;
                        }
                        return null;
                    })
                    .toArray(Skin[]::new);
        }
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tabSpecific ? "icon of tab " + column + ", " + row + " for " + playerExpression : "initial icon of " + playerExpression + "'s array tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tabSpecific = i == 0;
        if (tabSpecific) {
            column = (Expression<Number>) expressions[0];
            row = (Expression<Number>) expressions[1];
            playerExpression = (Expression<Player>) expressions[2];
        } else {
            playerExpression = (Expression<Player>) expressions[0];
        }
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Skin value = (Skin) delta[0];
        if (tabSpecific) {
            int column = this.column.getSingle(event).intValue();
            int row = this.row.getSingle(event).intValue();
            for (Player player : playerExpression.getArray(event)) {
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                    ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                    arrayTablist.getTab(column, row).setIcon(value);
                }
            }
        } else {
            for (Player player : playerExpression.getArray(event)) {
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                    ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                    arrayTablist.initialIcon = value;
                }
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
