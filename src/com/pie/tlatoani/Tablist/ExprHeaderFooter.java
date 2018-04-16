package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprHeaderFooter extends SimpleExpression<String> {
    TablistProvider tablistProvider;
    boolean header;

    @Override
    protected String[] get(Event event) {
        if (header) {
            return tablistProvider
                    .view(event)
                    .flatMap(tablist -> Arrays.stream(tablist.getHeader()))
                    .toArray(String[]::new);
        } else {
            return tablistProvider
                    .view(event)
                    .flatMap(tablist -> Arrays.stream(tablist.getHeader()))
                    .toArray(String[]::new);
        }
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
        return "tablist " + (header ? "header" : "footer") + " for " + tablistProvider;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        header = parseResult.mark == 0;
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        for (Tablist tablist : tablistProvider.get(event)) {
            String[] result;
            if (mode == Changer.ChangeMode.SET) {
                result = new String[delta.length];
                System.arraycopy(delta, 0, result, 0, delta.length);
            } else if (mode == Changer.ChangeMode.ADD) {
                String[] original = header ? tablist.getHeader() : tablist.getFooter();
                result = new String[original.length + delta.length];
                System.arraycopy(original, 0, result, 0, original.length);
                System.arraycopy(delta, 0, result, original.length, delta.length);
            } else if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
                result = new String[0];
            } else {
                throw new IllegalArgumentException("Illegal mode: " + mode);
            }
            if (header) {
                tablist.setHeader(result);
            } else {
                tablist.setFooter(result);
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
