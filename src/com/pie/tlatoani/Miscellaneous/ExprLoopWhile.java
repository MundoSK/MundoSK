package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.google.common.collect.Iterators;
import com.pie.tlatoani.Util.Collections.ConditionalIterator;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class ExprLoopWhile extends SimpleExpression<Object> {
    private Expression<?> objects;
    private Expression<Boolean> booleanExpression;
    private String pie;

    private boolean negate;
    private boolean indefinitely;

    @Override
    protected Object[] get(Event event) {
        throw new UnsupportedOperationException("'%objects% while %boolean%' should only be used in loops!!");
    }

    @Override
    public Iterator<?> iterator(Event event) {
        //return new UtilWhileClock(new ArrayList<>(Arrays.asList(objects.getArray(event))), event, booleanExpression);
        //return new ConditionalIterator(Iterators.cy)
        Supplier<Boolean> condition =
                negate ? () -> !booleanExpression.getSingle(event)
                        : () -> booleanExpression.getSingle(event);
        if (indefinitely) {
            return new ConditionalIterator(Iterators.cycle(objects.getSingle(event)), condition);
        } else {
            return new ConditionalIterator(objects.iterator(event), condition);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return objects.getReturnType();
    }

    @Override
    public String toString(Event event, boolean b) {
        return objects + " " + (
                indefinitely
                        ? (negate ? "until" : "while")
                        : (negate ? "unless" : "if")
                ) + " " + booleanExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        objects = expressions[0];
        if (objects.isSingle()) {
            Skript.error("You can't loop through a single object!");
            return false;
        }
        booleanExpression = (Expression<Boolean>) expressions[1];
        negate = i % 2 == 1;
        indefinitely = i < 2;
        return true;
    }
}
