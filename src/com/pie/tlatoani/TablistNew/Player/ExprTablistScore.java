package com.pie.tlatoani.TablistNew.Player;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class ExprTablistScore extends SimpleExpression<Number> {
    private Expression<Player> objectExpression;
    private Expression<Player> playerExpression;

    @Override
    protected Number[] get(Event event) {
        Player object = objectExpression.getSingle(event);
        Player[] players = playerExpression.getArray(event);
        Number[] scores = new Number[players.length];
        for (int i = 0; i < players.length; i++) {
            scores[i] = TablistManager
                    .getTablistOfPlayer(players[i])
                    .getPlayerTablist()
                    .flatMap(playerTablist -> playerTablist.getTab(object))
                    .map(tab -> tab.getScore())
                    .orElse(null);
        }
        return scores;
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "tablist score of " + objectExpression + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        objectExpression = (Expression<Player>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Integer value = mode == Changer.ChangeMode.SET ? ((Number) delta[0]).intValue() : null;
        Player object = objectExpression.getSingle(event);
        for (Player player : playerExpression.getArray(event)) {
            TablistManager
                    .getTablistOfPlayer(player)
                    .getPlayerTablist()
                    .flatMap(playerTablist -> playerTablist.forceTab(object))
                    .ifPresent(tab -> tab.setScore(value));
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
