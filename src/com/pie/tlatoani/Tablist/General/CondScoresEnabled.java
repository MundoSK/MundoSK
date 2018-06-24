package com.pie.tlatoani.Tablist.General;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class CondScoresEnabled extends SimpleExpression<Boolean> {
    private TablistProvider tablistProvider;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{tablistProvider.check(event, Tablist::areScoresEnabled, positive)};
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
        return tablistProvider.toString("scores are " + (positive ? "enabled" : "disabled") + " in tablist [of %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        positive = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        boolean enabled = positive == (boolean) delta[0];
        for (Tablist tablist : tablistProvider.get(event)) {
            tablist.setScoresEnabled(enabled);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
