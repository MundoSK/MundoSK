package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

import java.util.function.Function;

/**
 * Created by Tlatoani on 6/11/16.
 */
public class ExpressionModifier<T, R> extends SimpleExpression<R> {
    private Expression<T> expression;
    private Function<T, R> function;

    public ExpressionModifier(Expression<T> expression, Function<T, R> function) {
        this.expression = expression;
        this.function = function;
    }

    @Override
    protected R[] get(Event event) {
        //return consume(expression.getSingle(event));
        return CollectionUtils.array(function.apply(expression.getSingle(event)));
    }

    //protected abstract T[] consume(U arg);

    //protected R[] array(R... elements) {
    //    return elements;
    //}

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends R> getReturnType() {
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
