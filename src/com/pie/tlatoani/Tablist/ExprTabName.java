package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.SkinTexture.SkinManager;
import com.pie.tlatoani.SkinTexture.SkinTexture;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 10/31/16.
 */
public class ExprTabName extends SimpleExpression<String> {
    private Expression<Player> playerExpression;

    @Override
    protected String[] get(Event event) {
        return new String[]{playerExpression.getSingle(event).getPlayerListName()};
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
        return playerExpression + "'s tab name";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        String tabName = null;
        Player player = playerExpression.getSingle(event);
        if (mode == Changer.ChangeMode.SET)
            tabName = (String) delta[0];
        playerExpression.getSingle(event).setPlayerListName(tabName);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
