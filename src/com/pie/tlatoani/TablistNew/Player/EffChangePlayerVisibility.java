package com.pie.tlatoani.TablistNew.Player;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 10/20/16.
 */
public class EffChangePlayerVisibility extends Effect {
    private boolean visible;
    private Expression<Player> playerExpression;
    private Expression<Player> objectsExpression;

    @Override
    protected void execute(Event event) {
        Player[] objects = objectsExpression.getArray(event);
        if (visible) {
            for (Player player : playerExpression.getArray(event)) {
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                tablist.getPlayerTablist().ifPresent(playerTablist -> {
                    for (Player object : objects) {
                        playerTablist.showPlayer(object);
                    }
                });
            }
        } else {
            for (Player player : playerExpression.getArray(event)) {
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                tablist.getPlayerTablist().ifPresent(playerTablist -> {
                    for (Player object : objects) {
                        playerTablist.hidePlayer(object);
                    }
                });
            }
        }

    }

    @Override
    public String toString(Event event, boolean b) {
        return (visible ? "show " : "hide ") + objectsExpression + " in " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        visible = parseResult.mark == 0;
        playerExpression = (Expression<Player>) expressions[1];
        objectsExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
