package com.pie.tlatoani.Tablist.SkinTexture;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/3/16.
 */
public class ExprTextureWith extends SimpleExpression<SkinTexture> {
    private Expression<String> valueExpr;
    private Expression<String> signatureExpr;

    @Override
    protected SkinTexture[] get(Event event) {
        return new SkinTexture[]{new SkinTexture(valueExpr.getSingle(event), signatureExpr.getSingle(event))};
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
        return "skin texture with value " + valueExpr + " signature " + signatureExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        valueExpr = (Expression<String>) expressions[0];
        signatureExpr = (Expression<String>) expressions[1];
        return true;
    }
}
