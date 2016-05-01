package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import com.w00tmast3r.skquery.skript.LambdaCondition;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class UtilWhileClockPredicate implements Iterator{
    private UtilInfiniteIterator  infiniteIterator;
    private Event event;
    private LambdaCondition condition;

    public UtilWhileClockPredicate(List<?> list, Event event, Expression<?> condition) {
        this.infiniteIterator = new UtilInfiniteIterator(list);
        this.event = event;
        this.condition = (LambdaCondition) condition.getSingle(event);
    }

    @Override
    public boolean hasNext() {
        return condition.check(event);
    }

    @Override
    public Object next() {
        return infiniteIterator.next();
    }
}
