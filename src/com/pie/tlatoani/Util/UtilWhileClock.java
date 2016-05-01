package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class UtilWhileClock implements Iterator {
    private UtilInfiniteIterator  infiniteIterator;
    private Event event;
    private Expression<Boolean> booleanExpression = null;

    public UtilWhileClock(List<?> list, Event event, Expression<?> booleanExpression) {
        this.infiniteIterator = new UtilInfiniteIterator(list);
        this.event = event;
        this.booleanExpression = (Expression<Boolean>) booleanExpression;
    }

    @Override
    public boolean hasNext() {
        return booleanExpression.getSingle(event);
    }

    @Override
    public Object next() {
        return infiniteIterator.next();
    }
}
