package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/11/16.
 */
public abstract class ConsumerExpression<T, U> extends SimpleExpression<T> {
    private Expression<U> expression;

    public ConsumerExpression(Expression<U> expression) {
        this.expression = expression;
    }

    @Override
    protected T[] get(Event event) {
        return consume(expression.getSingle(event));
    }

    protected abstract T[] consume(U arg);

    protected T[] array(T... elements) {
        return elements;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends T> getReturnType() {
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
