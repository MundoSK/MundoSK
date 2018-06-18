package com.pie.tlatoani.Tablist;

import ch.njol.skript.lang.Expression;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.ListUtil.Transformer;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

import java.util.function.Function;

/**
 * Created by Tlatoani on 4/14/18.
 */
public class TransHeaderFooter implements Transformer<String>, Transformer.Resettable<String>, Transformer.Addable<String, String> {
    private TablistProvider tablistProvider;
    private boolean header;

    @Override
    public boolean init(Expression expression) {
        if (expression instanceof ExprHeaderFooter) {
            ExprHeaderFooter exprHeaderFooter = (ExprHeaderFooter) expression;
            tablistProvider = exprHeaderFooter.tablistProvider;
            header = exprHeaderFooter.header;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Class<? extends String> getType() {
        return String.class;
    }

    @Override
    public boolean isSettable() {
        return true;
    }

    @Override
    public String[] get(Event event) {
        return tablistProvider
                .view(event)
                .findFirst()
                .map(header ? Tablist::getHeader : Tablist::getFooter)
                .map(list -> list.toArray(new String[0]))
                .orElse(new String[0]);
    }

    @Override
    public void set(Event event, Function<Object[], Object[]> changer) {
        for (Tablist tablist : tablistProvider.get(event)) {
            if (header) {
                tablist.setHeader(ImmutableList.copyOf((String[]) changer.apply(tablist.getHeader().toArray(new String[0]))));
            } else {
                tablist.setFooter(ImmutableList.copyOf((String[]) changer.apply(tablist.getFooter().toArray(new String[0]))));
            }
        }
    }

    @Override
    public String add(String orig, String addend) {
        return orig + addend;
    }

    @Override
    public Class<? extends String> getAddendType() {
        return String.class;
    }

    @Override
    public String reset() {
        return "";
    }
}
