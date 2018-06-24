package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Core.Static.MathUtil;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class ExprLatencyBarsOfArrayTab extends SimpleExpression<Number> {
    private Expression<Number> column;
    private Expression<Number> row;
    private TablistProvider tablistProvider;

    @Override
    protected Number[] get(Event event) {
        int column = Optional.ofNullable(this.column.getSingle(event)).map(Number::intValue).orElse(-1);
        int row = Optional.ofNullable(this.row.getSingle(event)).map(Number::intValue).orElse(-1);
        return tablistProvider
                .view(event)
                .map(tablist -> OptionalUtil
                        .cast(tablist.getSupplementaryTablist(), ArrayTablist.class)
                        .filter(arrayTablist -> MathUtil.isInRange(1, column, arrayTablist.getColumns()))
                        .filter(arrayTablist -> MathUtil.isInRange(1, row, arrayTablist.getRows()))
                        .map(arrayTablist -> arrayTablist.getTab(column, row))
                        .flatMap(Tab::getLatencyBars)
                        .orElse(null))
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
        return tablistProvider.toString("latency bars of array tab " + column + ", " + row + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        column = (Expression<Number>) expressions[0];
        row = (Expression<Number>) expressions[1];
        tablistProvider = TablistProvider.of(expressions, 2);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        int column = Optional.ofNullable(this.column.getSingle(event)).map(Number::intValue).orElse(-1);
        int row = Optional.ofNullable(this.row.getSingle(event)).map(Number::intValue).orElse(-1);
        Integer value;
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            if (delta[0] == null) {
                return;
            }
            value = ((Number) delta[0]).intValue() * (mode == Changer.ChangeMode.ADD ? 1 : -1);
        } else if (mode == Changer.ChangeMode.SET && delta[0] != null) {
            value = MathUtil.limitToRange(0, ((Number) delta[0]).intValue(), 5);
        } else {
            value = null;
        }
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                if (MathUtil.isInRange(1, column, arrayTablist.getColumns()) && MathUtil.isInRange(1, row, arrayTablist.getRows())) {
                    Tab tab = arrayTablist.getTab(column, row);
                    if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
                        tab.getLatencyBars().ifPresent(original ->
                                tab.setLatencyBars(MathUtil.limitToRange(0, original + value, 5))
                        );
                    } else {
                        tab.setLatencyBars(value);
                    }
                }
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
