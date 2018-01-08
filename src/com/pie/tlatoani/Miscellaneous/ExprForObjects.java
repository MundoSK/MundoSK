package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 1/3/17.
 */
public class ExprForObjects extends SimpleExpression<Object> {
    Expression function;
    Variable container;
    Expression list;

    @Override
    protected Object[] get(Event event) {
        return Arrays.stream(list.getArray(event)).flatMap(val -> {
            container.change(event, new Object[]{val}, Changer.ChangeMode.SET);
            return Arrays.stream(function.getArray(event));
        }).toArray(Object[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return function.getReturnType();
    }

    @Override
    public String toString(Event event, boolean b) {
        return function + " for " + container + " in " + list;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        function = expressions[0];
        /*if (i == 1) {
            if (!(function instanceof ExprThatAre)) {
                return false;
            }
        }*/
        list = expressions[2];
        if (!(expressions[1] instanceof Variable)) {
            return false;
        }
        container = (Variable) expressions[1];
        return true;
    }
}
