package com.pie.tlatoani.Tablist.Player;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 10/20/16.
 */
public class EffShowHidePlayerTab extends Effect {
    private boolean visible;
    private TablistProvider tablistProvider;
    private Expression<Player> objectsExpression;

    @Override
    protected void execute(Event event) {
        Player[] objects = objectsExpression.getArray(event);
        if (objects == null) {
            return;
        }
        for (Tablist tablist : tablistProvider.get(event)) {
            tablist.getPlayerTablist().ifPresent(playerTablist -> {
                for (Player object : objects) {
                    if (object.isOnline()) {
                        if (visible) {
                            playerTablist.showPlayer(object);
                        } else {
                            playerTablist.hidePlayer(object);
                        }
                    }
                }
            });
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString((visible ? "show " : "hide ") + objectsExpression
                + "'s player tab" + (objectsExpression.isSingle() ? "" : "s") + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        visible = parseResult.mark == 0;
        tablistProvider = TablistProvider.of(expressions, 1);
        objectsExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
