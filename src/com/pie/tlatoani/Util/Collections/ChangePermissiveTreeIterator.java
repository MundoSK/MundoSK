package com.pie.tlatoani.Util.Collections;

import com.pie.tlatoani.Core.Static.Logging;

import java.util.*;

/**
 * Created by Tlatoani on 12/9/17.
 */
public class ChangePermissiveTreeIterator<K, V> implements Iterator<V> {
    private final TreeMap<K, V> treeMap;
    private K nextKey = null;
    private V nextValue = null;
    private ChangePermissiveTreeIterator<K, V> subIterator = null;
    private Optional<Boolean> hasNext = Optional.empty();

    public ChangePermissiveTreeIterator(TreeMap<K, V> treeMap) {
        this.treeMap = treeMap;
    }

    @Override
    public boolean hasNext() {
        Logging.debug(this, this + "hasNext() called, nextKey = " + nextKey + ", nextValue = " + nextValue + ", subIterator = " + subIterator + ", hasNext = " + hasNext);
        if (hasNext.isPresent()) {
            return hasNext.get();
        }
        if (subIterator != null && subIterator.hasNext() && subIterator.treeMap == treeMap.get(nextKey)) {
            Logging.debug(this, this + "hasNext(): subIterator continues");
            nextValue = subIterator.next();
            hasNext = Optional.of(true);
            return true;
        } else {
            Logging.debug(this, this + "hasNext(): subIterator ends");
        }
        Logging.debug(this, this + "hasNext(): getting next value");
        Map.Entry<K, V> nextEntry = nextKey == null ? treeMap.firstEntry() : treeMap.higherEntry(nextKey);
        if (nextEntry == null) {
            nextKey = null;
            nextValue = null;
            Logging.debug(this, this + "hasNext(): returning false");
            hasNext = Optional.of(false);
            return false;
        } else {
            nextKey = nextEntry.getKey();
            if (nextEntry.getValue() instanceof TreeMap) {
                subIterator = new ChangePermissiveTreeIterator((TreeMap<K, V>) nextEntry.getValue());
                Logging.debug(this, this + "hasNext(): testing new subIterator");
                return hasNext();
            } else {
                nextValue = nextEntry.getValue();
                Logging.debug(this, this + "hasNext(): returning true");
                hasNext = Optional.of(true);
                return true;
            }
        }
    }

    @Override
    public V next() {
        Logging.debug(this, this + "next() called, nextKey = " + nextKey + ", nextValue = " + nextValue + ", subIterator = " + subIterator + ", hasNext = " + hasNext);
        if (!hasNext()) {
            throw new NoSuchElementException("Called next() on a TreeIterator without a next element");
        }
        hasNext = Optional.empty();
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
