package com.pie.tlatoani.Tablist.General;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.Core.Static.MathUtil;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.event.Event;

import java.util.List;

public class ExprHeightOfHeaderFooter extends SimpleExpression<Number> {
    TablistProvider tablistProvider;
    boolean header;

    @Override
    protected Number[] get(Event event) {
        return tablistProvider
                .view(event)
                .map(header ? Tablist::getHeader : Tablist::getFooter)
                .map(List::size)
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
        return tablistProvider.toString("height of tablist " + (header ? "header" : "footer") + " [of %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        header = parseResult.mark == 0;
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (delta[0] == null) {
            return;
        }
        int value = ((Number) delta[0]).intValue();
        for (Tablist tablist : tablistProvider.get(event)) {
            ImmutableList<String> original = header ? tablist.getHeader() : tablist.getFooter();
            int newHeight = MathUtil.change(mode, original.size(), value);
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            for (int i = 0; i < newHeight; i++) {
                builder.add(i < original.size() ? original.get(i) : "");
            }
            if (header) {
                tablist.setHeader(builder.build());
            } else {
                tablist.setFooter(builder.build());
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
