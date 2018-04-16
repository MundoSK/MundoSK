package com.pie.tlatoani.Tablist.Player;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/13/17.
 */
public class CondPlayerIsVisible extends SimpleExpression<Boolean> {
    private TablistProvider tablistProvider;
    private Expression<Player> objectExpression;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        Player object = objectExpression.getSingle(event);
        if (!object.isOnline()) {
            return new Boolean[]{false};
        }
        return new Boolean[]{tablistProvider.check(event, tablist -> tablist.isPlayerVisible(object), positive)};
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
        return objectExpression + "'s player tab is " + (positive ? "visible" : "hidden") + " for " + tablistProvider;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 1);
        objectExpression = (Expression<Player>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }
}
