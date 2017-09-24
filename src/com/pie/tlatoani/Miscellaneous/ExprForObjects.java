package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/3/17.
 */
public class ExprForObjects extends SimpleExpression<Object> {
    Expression function;
    Expression container;
    Expression list;

    @Override
    protected Object[] get(Event event) {
        return new Object[0];
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return null;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (i == 0) {
            function = expressions[0];
            container = expressions[1];
            list = expressions[2];
            Class[] posTypes = container.acceptChange(Changer.ChangeMode.SET);

        }
        return false;
    }
}
