package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 9/18/16.
 */
public class ExprDisplayedSkinOfPlayer extends SimpleExpression<Skin> {
    private Expression<Player> playerExpression;

    @Override
    protected Skin[] get(Event event) {
        return new Skin[]{SkinManager.getDisplayedSkin(playerExpression.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return playerExpression + "'s displayed skin";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Skin skinDelta = null;
        Player player = playerExpression.getSingle(event);
        if (mode == Changer.ChangeMode.SET)
            skinDelta = (Skin) delta[0];
        SkinManager.setDisplayedSkin(player, skinDelta);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        if (mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array();
        }
        return null;
    }
}
