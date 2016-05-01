package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class ExprLoopWhile extends SimpleExpression<Object> {
    private Expression<?> objects;
    private Expression<?> booleanExpression;
    private Boolean ifBooleanExpression;

    @Override
    protected Object[] get(Event event) {
        throw new UnsupportedOperationException("'%objects% while %boolean%' should only be used in loops!!");
    }

    @Override
    public Iterator<?> iterator(Event event) {
        if (ifBooleanExpression) {
            return new UtilWhileClock(Arrays.asList(objects.getSingle(event)), event, booleanExpression);
        } else {
            return new UtilWhileClockPredicate(Arrays.asList(objects.getSingle(event)), event, booleanExpression);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%objects% while %boolean%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        objects = expressions[0];
        booleanExpression = expressions[1];
        ifBooleanExpression = booleanExpression.getReturnType() == Boolean.class;
        return true;
    }
}
