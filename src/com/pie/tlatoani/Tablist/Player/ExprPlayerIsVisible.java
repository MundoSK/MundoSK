package com.pie.tlatoani.Tablist.Player;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 8/13/17.
 */
public class ExprPlayerIsVisible extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpression;
    private Expression<Player> objectExpression;

    @Override
    protected Boolean[] get(Event event) {
        Player object = objectExpression.getSingle(event);
        return Arrays
                .stream(playerExpression.getArray(event))
                .map(player -> TablistManager.getTablistOfPlayer(player).isPlayerVisible(object))
                .toArray(Boolean[]::new);
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
