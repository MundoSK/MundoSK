package com.pie.tlatoani.SkinTexture;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/3/16.
 */
public class ExprTextureOfPlayer extends SimpleExpression<SkinTexture> {
    private Expression<Player> playerExpression;

    @Override
    protected SkinTexture[] get(Event event) {
        return new SkinTexture[]{new SkinTexture(playerExpression.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends SkinTexture> getReturnType() {
        return SkinTexture.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "skin texture of " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
