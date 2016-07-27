package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.TabListIcon;
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprIconOfTab extends SimpleExpression<Object> {
    private Expression<String> id;
    private Expression<Player> playerExpression;

    @Override
    protected Object[] get(Event event) {
        SimpleTabList simpleTabList;
        TabListIcon icon;
        return new Object[] {
                (simpleTabList = TabListManager.getSimpleTabListForPlayer(playerExpression.getSingle(event))) != null ?
                        simpleTabList.getHead(id.getSingle(event)).convertTabListIconToSkriptValue() :
                        null
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
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
        SimpleTabList simpleTabList;
        if ((simpleTabList = TabListManager.getSimpleTabListForPlayer(playerExpression.getSingle(event))) != null) {
            simpleTabList.setHead(id.getSingle(event), TabListIcon.convertSkriptValueToTabListIcon(delta[0]));
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class, OfflinePlayer.class);
        }
        return null;
    }
}
