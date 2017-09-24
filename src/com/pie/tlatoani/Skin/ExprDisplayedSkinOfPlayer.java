package com.pie.tlatoani.Skin;

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
 * Created by Tlatoani on 9/18/16.
 */
public class ExprDisplayedSkinOfPlayer extends SimpleExpression<Skin> {
    private Expression<Player> playerExpression;
    private Expression<Player> targetExpression;
    private Expression<Player> excludeExpression;

    @Override
    protected Skin[] get(Event event) {
        Player player = playerExpression.getSingle(event);
        if (targetExpression == null) {
            return new Skin[]{SkinManager.getDisplayedSkin(player)};
        } else {
            return Arrays.stream(targetExpression.getArray(event)).map(target -> SkinManager.getPersonalDisplayedSkin(player, target)).toArray(Skin[]::new);
        }
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
        return playerExpression + "'s displayed skin" + (targetExpression == null ? "" : " for " + targetExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        targetExpression = (Expression<Player>) expressions[1];
        excludeExpression = (Expression<Player>) expressions[2];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Skin skinDelta = null;
        Player player = playerExpression.getSingle(event);
        if (mode == Changer.ChangeMode.SET)
            skinDelta = (Skin) delta[0];
        if (targetExpression != null) {
            SkinManager.setPersonalDisplayedSkin(player, Arrays.asList(targetExpression.getArray(event)), skinDelta);
        } else if (excludeExpression != null) {
            SkinManager.setDisplayedSkinExcluding(player, Arrays.asList(excludeExpression.getArray(event)), skinDelta);
        } else {
            SkinManager.setDisplayedSkin(player, skinDelta);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
            return CollectionUtils.array();
        }
        return null;
    }
}
