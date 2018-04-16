package com.pie.tlatoani.Util.Collections;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Created by Tlatoani on 3/18/17.
 */
public class ConditionalIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final Supplier<Boolean> condition;

    public ConditionalIterator(Iterator<T> iterator, Supplier<Boolean> condition) {
        this.iterator = iterator;
        this.condition = condition;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() && condition.get();
    }

    @Override
    public T next() {
        return iterator.next();
    }
}
