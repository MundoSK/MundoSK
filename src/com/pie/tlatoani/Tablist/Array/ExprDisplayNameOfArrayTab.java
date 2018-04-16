package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class ExprDisplayNameOfArrayTab extends SimpleExpression<String> {
    private Expression<Number> column;
    private Expression<Number> row;
    private TablistProvider tablistProvider;

    @Override
    protected String[] get(Event event) {
        int column = this.column.getSingle(event).intValue();
        int row = this.row.getSingle(event).intValue();
        return tablistProvider
                .view(event)
                .map(tablist -> {
                    if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                        ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                        return arrayTablist.getTab(column, row).getDisplayName();
                    }
                    return null;
                })
                .toArray(String[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "display name of tab " + column + ", " + row + " for " + tablistProvider;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        column = (Expression<Number>) expressions[0];
        row = (Expression<Number>) expressions[1];
        tablistProvider = TablistProvider.of(expressions, 2);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        int column = this.column.getSingle(event).intValue();
        int row = this.row.getSingle(event).intValue();
        String value = mode == Changer.ChangeMode.SET ? (String) delta[0] : null;
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                arrayTablist.getTab(column, row).setDisplayName(value);
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
