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
 * Created by Tlatoani on 11/25/16.
 */
public class ExprScoresEnabled extends SimpleExpression<Boolean> {
    private Expression<Tablist> tablistExpression;
    private Expression<Player> playerExpression;

    @Override
    protected Boolean[] get(Event event) {
        Tablist tablist = tablistExpression != null ? tablistExpression.getSingle(event) : Tablist.getTablistForPlayer(playerExpression.getSingle(event));
        return new Boolean[]{tablist.areScoresEnabled()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "scores enabled";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistExpression = (Expression<Tablist>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Tablist tablist = tablistExpression != null ? tablistExpression.getSingle(event) : Tablist.getTablistForPlayer(playerExpression.getSingle(event));
        tablist.setScoresEnabled((Boolean) delta[0]);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
