package com.pie.tlatoani.Skin;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 3/5/17.
 */
@Deprecated
public class ExprCombinedSkin extends SimpleExpression<Skin> {

    @Override
    protected Skin[] get(Event event) {
        throw new UnsupportedOperationException("ExprCombinedSkin should not be used!");
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
        return "combined skin (Unsupported)";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Skript.error("The 'combined skin' expression is not supported anymore!");
        return false;
    }
}
