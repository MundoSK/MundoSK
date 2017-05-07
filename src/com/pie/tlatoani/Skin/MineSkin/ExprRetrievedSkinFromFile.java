package com.pie.tlatoani.Skin.MineSkin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/6/17.
 */
public class ExprRetrievedSkinFromFile extends SimpleExpression<Skin> {
    private Expression<String> stringExpr;
    private boolean file;

    @Override
    protected Skin[] get(Event event) {

        return new Skin[0];
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
        return "retrieved skin from " + stringExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        stringExpr = (Expression<String>) expressions[0];
        file = i == 0;
        return true;
    }
}
