package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/23/16.
 */
public class ExprDimensionOfArrayTablist extends SimpleExpression<Number> {
    private TablistProvider tablistProvider;
    private boolean isColumns;

    @Override
    protected Number[] get(Event event) {
        return tablistProvider
                .view(event)
                .map(tablist -> {
                    if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                        ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                        return isColumns ? arrayTablist.getColumns() : arrayTablist.getRows();
                    }
                    return null;
                })
                .toArray(Number[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("amount of " + (isColumns ? "column" : "row") + "s in [%'s] array tablist");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        isColumns = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        int value = Optional.ofNullable((Number) delta[0]).map(Number::intValue).orElse(0);
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                if (isColumns) {
                    if (value > 0) {
                        arrayTablist.setColumns(value);
                    } else {
                        tablist.setSupplementaryTablist(SimpleTablist::new);
                    }
                } else {
                    arrayTablist.setRows(value);
                }
            } else if (isColumns && value > 0) {
                tablist.setSupplementaryTablist(ArrayTablist.create(value, 20));
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
