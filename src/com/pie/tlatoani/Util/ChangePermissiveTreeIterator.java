package com.pie.tlatoani.Util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Created by Tlatoani on 12/9/17.
 */
public class ChangePermissiveTreeIterator<K, V> implements Iterator<V> {
    private final TreeMap<K, V> treeMap;
    private K nextKey = null;
    private V nextValue = null;
    private ChangePermissiveTreeIterator<K, V> subIterator = null;
    private boolean calledHasNext = false;

    public ChangePermissiveTreeIterator(TreeMap<K, V> treeMap) {
        this.treeMap = treeMap;
    }

    @Override
    public boolean hasNext() {
        if (calledHasNext) {
            return nextKey != null;
        }
        try {
            if (subIterator != null && subIterator.hasNext() && subIterator.treeMap == treeMap.get(nextKey)) {
                nextValue = subIterator.next();
                return true;
            }
            Map.Entry<K, V> nextEntry = nextKey == null ? treeMap.firstEntry() : treeMap.higherEntry(nextKey);
            if (nextEntry == null) {
                nextKey = null;
                nextValue = null;
                return false;
            } else {
                nextKey = nextEntry.getKey();
                if (nextEntry.getValue() instanceof TreeMap) {
                    subIterator = new ChangePermissiveTreeIterator((TreeMap<K, V>) nextEntry.getValue());
                    return hasNext();
                } else {
                    nextValue = nextEntry.getValue();
                    return true;
                }
            }
        } finally {
            calledHasNext = true;
        }
    }

    @Override
    public V next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Called next() on a TreeIterator without a next element");
        }
        calledHasNext = false;
        return nextValue;
    }

    public String getBranch() {
        if (subIterator == null) {
            return nextKey.toString();
        } else {
            return nextKey + "::" + subIterator.getBranch();
        }
    }
}
