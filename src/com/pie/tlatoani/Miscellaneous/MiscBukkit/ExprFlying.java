package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/17/17.
 */
public class ExprFlying extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpression;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{playerExpression.getSingle(event).isFlying()};
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
        return playerExpression + "is flying";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        playerExpression.getSingle(event).setFlying((Boolean) delta[0]);
    }

    @Override
    public Class[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }
}
