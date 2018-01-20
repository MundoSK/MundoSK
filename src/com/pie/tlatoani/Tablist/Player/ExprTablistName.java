package com.pie.tlatoani.Tablist.Player;


import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class ExprTablistName extends SimpleExpression<String> {
    private Expression<Player> objectExpression;
    private Expression<Player> playerExpression;

    @Override
    protected String[] get(Event event) {
        Player object = objectExpression.getSingle(event);
        if (!object.isOnline()) {
            return new String[0];
        }
        return (playerExpression == null ? Bukkit.getOnlinePlayers().stream() : Arrays.stream(playerExpression.getArray(event)))
                .filter(Player::isOnline)
                .map(player -> TablistManager
                        .getTablistOfPlayer(player)
                        .getPlayerTablist()
                        .flatMap(playerTablist -> playerTablist.getTab(object))
                        .map(Tab::getDisplayName)
                        .orElse(null))
                .toArray(String[]::new);
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "tablist name of " + objectExpression + (playerExpression == null ? "" : " for " + playerExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        objectExpression = (Expression<Player>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String value = mode == Changer.ChangeMode.SET ? (String) delta[0] : null;
        Player object = objectExpression.getSingle(event);
        if (!object.isOnline()) {
            return;
        }
        for (Player player : playerExpression == null ? Bukkit.getOnlinePlayers() : Arrays.asList(playerExpression.getArray(event))) {
            if (!player.isOnline()) {
                continue;
            }
            TablistManager
                    .getTablistOfPlayer(player)
                    .getPlayerTablist()
                    .flatMap(playerTablist -> playerTablist.forceTab(object))
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
