package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class ExprScoresEnabled extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpression;

    @Override
    protected Boolean[] get(Event event) {
        return Arrays
                .stream(playerExpression.getArray(event))
                .map(player -> TablistManager.getTablistOfPlayer(player).areScoresEnabled())
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
        return "scores are enabled in " + playerExpression + "'s tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        boolean enabled = (boolean) delta[0];
        for (Player player : playerExpression.getArray(event)) {
            TablistManager.getTablistOfPlayer(player).setScoresEnabled(enabled);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
