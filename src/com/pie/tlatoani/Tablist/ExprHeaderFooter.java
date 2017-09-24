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
 * Created by Tlatoani on 8/18/17.
 */
public class ExprHeaderFooter extends SimpleExpression<String> {
    private Expression<Player> playerExpression;
    private boolean header;

    @Override
    protected String[] get(Event event) {
        if (header) {
            return Arrays
                    .stream(playerExpression.getArray(event))
                    .filter(Player::isOnline)
                    .flatMap(player -> Arrays.stream(TablistManager.getTablistOfPlayer(player).getHeader()))
                    .toArray(String[]::new);
        } else {
            return Arrays
                    .stream(playerExpression.getArray(event))
                    .filter(Player::isOnline)
                    .flatMap(player -> Arrays.stream(TablistManager.getTablistOfPlayer(player).getFooter()))
                    .toArray(String[]::new);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "tablist " + (header ? "header" : "footer") + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        header = parseResult.mark == 0;
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        for (Player player : playerExpression.getArray(event)) {
            if (!player.isOnline()) {
                continue;
            }
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            String[] result;
            if (mode == Changer.ChangeMode.SET) {
                result = new String[delta.length];
                System.arraycopy(delta, 0, result, 0, delta.length);
            } else if (mode == Changer.ChangeMode.ADD) {
                String[] original = header ? tablist.getHeader() : tablist.getFooter();
                result = new String[original.length + delta.length];
                System.arraycopy(original, 0, result, 0, original.length);
                System.arraycopy(delta, 0, result, original.length, delta.length);
            } else if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
                result = new String[0];
            } else {
                throw new IllegalArgumentException("Illegal mode: " + mode);
            }
            if (header) {
                tablist.setHeader(result);
            } else {
                tablist.setFooter(result);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String[].class);
        }
        return null;
    }
}
