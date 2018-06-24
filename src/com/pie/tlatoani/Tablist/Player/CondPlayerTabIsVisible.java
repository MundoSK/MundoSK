package com.pie.tlatoani.Tablist.Player;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/13/17.
 */
public class CondPlayerTabIsVisible extends SimpleExpression<Boolean> {
    private TablistProvider tablistProvider;
    private Expression<Player> objectExpression;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        Player object = objectExpression.getSingle(event);
        if (object == null || !object.isOnline()) {
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
        return tablistProvider.toString(objectExpression + "'s player tab is " + (positive ? "visible" : "hidden") + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 1);
        objectExpression = (Expression<Player>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Player object = objectExpression.getSingle(event);
        if (object == null || delta[0] == null || !object.isOnline()) {
            return;
        }
        Boolean visible = positive == (Boolean) delta[0];
        for (Tablist tablist : tablistProvider.get(event)) {
            tablist.getPlayerTablist().ifPresent(playerTablist -> {
                if (visible) {
                    playerTablist.showPlayer(object);
                } else {
                    playerTablist.hidePlayer(object);
                }
            });
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
