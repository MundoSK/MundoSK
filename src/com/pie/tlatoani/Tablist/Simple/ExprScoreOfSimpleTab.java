package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class ExprScoreOfSimpleTab extends SimpleExpression<Number> {
    private Expression<String> id;
    private TablistProvider tablistProvider;

    @Override
    protected Number[] get(Event event) {
        String id = this.id.getSingle(event);
        return tablistProvider
                .view(event)
                .map(tablist -> {
                    if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                        SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                        return simpleTablist.getTab(id).map(Tab::getScore).orElse(null);
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
        return "score of simple tab " + id + " for " + tablistProvider;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String id = this.id.getSingle(event);
        Integer value = mode == Changer.ChangeMode.RESET ? null : ((mode == Changer.ChangeMode.REMOVE ? -1 : 1) * ((Number) delta[0]).intValue());
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.getTab(id).ifPresent(tab -> {
                    if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
                        tab.setScore(tab.getScore().orElse(0) + value);
                    } else {
                        tab.setScore(value);
                    }
                });
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
