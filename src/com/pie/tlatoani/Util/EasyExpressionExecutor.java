package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/28/16.
 */
public class EasyExpressionExecutor extends SimpleExpression<Object> {

    public interface EasyExpression<T> {

        T[] get(Event event, Object[] args);

    }

    public static <T> void registerEasyExpression(Class<T> returnType, Boolean isSingle, EasyExpression<T> expression, String... patterns) {

    }

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
        return false;
    }
}
