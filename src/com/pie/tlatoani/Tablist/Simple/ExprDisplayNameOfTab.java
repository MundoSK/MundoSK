package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprDisplayNameOfTab extends SimpleExpression<String> {
    private Expression<String> id;
    private Expression<Player> playerExpression;

    @Override
    protected String[] get(Event event) {
        String id = this.id.getSingle(event);
        return Arrays
                .stream(playerExpression.getArray(event))
                .filter(Player::isOnline)
                .map(player -> {
                    Tablist tablist = TablistManager.getTablistOfPlayer(player);
                    if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                        SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                        return simpleTablist.getTab(id).map(Tab::getDisplayName).orElse(null);
                    }
                    return null;
                })
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
        return "display name of simple tab " + id + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String id = this.id.getSingle(event);
        String value = (String) delta[0];
        for (Player player : playerExpression.getArray(event)) {
            if (!player.isOnline()) {
                continue;
            }
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.getTab(id).ifPresent(tab -> tab.setDisplayName(value));
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
