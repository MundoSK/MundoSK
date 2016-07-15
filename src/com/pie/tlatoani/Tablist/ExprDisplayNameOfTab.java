package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprDisplayNameOfTab extends SimpleExpression<String> {
    private Expression<String> id;
    private Expression<Player> playerExpression;

    @Override
    protected String[] get(Event event) {
        TabListManager tabListManager;
        return new String[] {
                (tabListManager = TabListManager.getForPlayer(playerExpression.getSingle(event))) != null ?
                        tabListManager.getDisplayName(id.getSingle(event)) :
                        null
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "display name of tab id " + id + " for " + playerExpression;
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
            tabListManager.setDisplayName(id.getSingle(event), (String) delta[0]);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
