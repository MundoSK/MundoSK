package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Expression;
import com.google.common.collect.Iterators;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class UtilWhileClock implements Iterator {
    private CircularIterator infiniteIterator;
    private Event event;
    private Expression<Boolean> booleanExpression = null;

    public UtilWhileClock(ArrayList<?> list, Event event, Expression<Boolean> booleanExpression) {
        this.infiniteIterator = new CircularIterator(list);
        Iterators.cycle(list);
        this.event = event;
        this.booleanExpression = booleanExpression;
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
