package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/8/18.
 */
public class CondArrayTablistEnabled extends SimpleExpression<Boolean> {
    private TablistProvider tablistProvider;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{tablistProvider.check(event, tablist -> tablist.getSupplementaryTablist() instanceof ArrayTablist, positive)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("array tablist is " + (positive ? "enabled" : "disabled") + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        positive = parseResult.mark == 0;
        return true;
    }
}
