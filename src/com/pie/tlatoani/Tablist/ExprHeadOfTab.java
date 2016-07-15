package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprHeadOfTab extends SimpleExpression<OfflinePlayer> {
    private Expression<String> id;
    private Expression<Player> playerExpression;

    @Override
    protected OfflinePlayer[] get(Event event) {
        TabListManager tabListManager;
        UUID headUUID;
        return new OfflinePlayer[] {
                (tabListManager = TabListManager.getForPlayer(playerExpression.getSingle(event))) != null
                        && (headUUID = tabListManager.getHead(id.getSingle(event))) != null ?
                        Bukkit.getOfflinePlayer(headUUID) :
                        null
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends OfflinePlayer> getReturnType() {
        return OfflinePlayer.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "head icon of tab id " + id + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        TabListManager tabListManager;
        if ((tabListManager = TabListManager.getForPlayer(playerExpression.getSingle(event))) != null) {
            if (mode == Changer.ChangeMode.SET) {
                tabListManager.setHead(id.getSingle(event), ((OfflinePlayer) delta[0]).getUniqueId());
            } else {
                tabListManager.setHead(id.getSingle(event), null);
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE) {
            return CollectionUtils.array(OfflinePlayer.class);
        }
        return null;
    }
}
