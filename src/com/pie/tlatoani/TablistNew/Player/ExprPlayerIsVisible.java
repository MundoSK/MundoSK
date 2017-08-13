package com.pie.tlatoani.TablistNew.Player;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/13/17.
 */
public class ExprPlayerIsVisible extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpression;
    private Expression<Player> objectExpression;

    @Override
    protected Boolean[] get(Event event) {
        Player[] players = playerExpression.getArray(event);
        Player object = objectExpression.getSingle(event);
        Boolean[] visibilities = new Boolean[players.length];
        for (int i = 0; i < players.length; i++) {
            visibilities[i] = TablistManager.getTablistOfPlayer(players[i]).isPlayerVisible(object);
        }
        return visibilities;
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return objectExpression + " is visible in " + playerExpression + "'s tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[1];
        objectExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
