package com.pie.tlatoani.Util;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class UtilInfiniteIterator implements Iterator {
    private List list;
    private Iterator iterator;

    public UtilInfiniteIterator(List list) {
        this.list = list;
        this.iterator = list.iterator();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Object next() {
        if (!iterator.hasNext()) {
            iterator = list.iterator();
        }
        return iterator.next();
    }
}
