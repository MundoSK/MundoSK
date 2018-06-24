package com.pie.tlatoani.Tablist.Player;


import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class ExprDisplayNameOfPlayerTab extends SimpleExpression<String> {
    private Expression<Player> objectExpression;
    private TablistProvider tablistProvider;

    @Override
    protected String[] get(Event event) {
        Player object = objectExpression.getSingle(event);
        if (object == null || !object.isOnline()) {
            return new String[0];
        }
        return tablistProvider
                .view(event)
                .map(tablist -> tablist
                        .getPlayerTablist()
                        .flatMap(playerTablist -> playerTablist.getTabIfModified(object))
                        .flatMap(Tab::getDisplayName)
                        .orElse(null))
                .toArray(String[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("display name of " + objectExpression + "'s player tab [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        objectExpression = (Expression<Player>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String value = mode == Changer.ChangeMode.SET ? (String) delta[0] : null;
        Player object = objectExpression.getSingle(event);
        if (object == null || !object.isOnline()) {
            return;
        }
        for (Tablist tablist : tablistProvider.get(event)) {
            tablist
                    .getPlayerTablist()
                    .flatMap(playerTablist -> playerTablist.getTab(object))
                    .ifPresent(tab -> tab.setDisplayName(value));
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
