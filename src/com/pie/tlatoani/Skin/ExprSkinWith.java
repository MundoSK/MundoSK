package com.pie.tlatoani.Skin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/3/16.
 */
public class ExprSkinWith extends SimpleExpression<Skin> {
    private Expression<String> valueExpr;
    private Expression<String> signatureExpr;

    @Override
    protected Skin[] get(Event event) {
        return new Skin[]{new Skin(valueExpr.getSingle(event), signatureExpr.getSingle(event))};
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
        return "skin with value " + valueExpr + " signature " + signatureExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        valueExpr = (Expression<String>) expressions[0];
        signatureExpr = (Expression<String>) expressions[1];
        return true;
    }
}
