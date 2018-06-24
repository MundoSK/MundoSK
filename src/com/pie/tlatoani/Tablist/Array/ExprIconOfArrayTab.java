package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Core.Static.MathUtil;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/25/16.
 */
public class ExprIconOfArrayTab extends SimpleExpression<Skin> {
    private Expression<Number> column;
    private Expression<Number> row;
    private TablistProvider tablistProvider;

    @Override
    protected Skin[] get(Event event) {
        int column = Optional.ofNullable(this.column.getSingle(event)).map(Number::intValue).orElse(-1);
        int row = Optional.ofNullable(this.row.getSingle(event)).map(Number::intValue).orElse(-1);
        return tablistProvider
                .view(event)
                .map(tablist -> OptionalUtil
                        .cast(tablist.getSupplementaryTablist(), ArrayTablist.class)
                        .filter(arrayTablist -> MathUtil.isInRange(1, column, arrayTablist.getColumns()))
                        .filter(arrayTablist -> MathUtil.isInRange(1, row, arrayTablist.getRows()))
                        .map(arrayTablist -> arrayTablist.getTab(column, row))
                        .flatMap(Tab::getIcon)
                        .orElse(null))
                .toArray(Skin[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("icon of array tab " + column + ", " + row + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        column = (Expression<Number>) expressions[0];
        row = (Expression<Number>) expressions[1];
        tablistProvider = TablistProvider.of(expressions, 2);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Skin value = mode == Changer.ChangeMode.SET ? (Skin) delta[0] : null;
        int column = Optional.ofNullable(this.column.getSingle(event)).map(Number::intValue).orElse(-1);
        int row = Optional.ofNullable(this.row.getSingle(event)).map(Number::intValue).orElse(-1);
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                ArrayTablist arrayTablist = (ArrayTablist) tablist.getSupplementaryTablist();
                if (MathUtil.isInRange(1, column, arrayTablist.getColumns()) && MathUtil.isInRange(1, row, arrayTablist.getRows())) {
                    arrayTablist.getTab(column, row).setIcon(value);
                }
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
