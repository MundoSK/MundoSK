package com.pie.tlatoani.Tablist.General;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprHeaderFooter extends SimpleExpression<String> {
    TablistProvider tablistProvider;
    boolean header;

    @Override
    protected String[] get(Event event) {
        return tablistProvider
                .view(event)
                .map(header ? Tablist::getHeader : Tablist::getFooter)
                .flatMap(List::stream)
                .toArray(String[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("tablist " + (header ? "header" : "footer") + " [of %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        header = parseResult.mark == 0;
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        for (int i = 0; i < delta.length; i++) {
            if (delta[i] == null) {
                delta[i] = "";
            }
        }
        for (Tablist tablist : tablistProvider.get(event)) {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            if (mode == Changer.ChangeMode.SET) {
                for (Object strO : delta) {
                    builder.add((String) strO);
                }
            } else if (mode == Changer.ChangeMode.ADD) {
                ImmutableList<String> original = header ? tablist.getHeader() : tablist.getFooter();
                builder.addAll(original);
                for (Object strO : delta) {
                    builder.add((String) strO);
                }
            } else if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
                //do nothing, builder is already empty
            } else {
                throw new IllegalArgumentException("Illegal mode: " + mode);
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
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String[].class);
        }
        return null;
    }
}
