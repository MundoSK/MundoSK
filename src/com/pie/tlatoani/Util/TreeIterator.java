package com.pie.tlatoani.Util;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class TreeIterator implements Iterator {
    private Iterator<Map.Entry<String, Object>> baseIterator;
    private TreeIterator subIterator = null;
    private String currentIndex;
    private String currentPrefix;
    private String nextIndex = null;
    private Object next = null;

    public TreeIterator(Map<String, Object> treeMap) {
        baseIterator = treeMap.entrySet().iterator();
    }

    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }
        if (subIterator != null) {
            if (subIterator.hasNext()) {
                next = subIterator.next;
                nextIndex = currentPrefix + "::" + subIterator.currentIndex();
                return true;
            }
            subIterator = null;
            currentPrefix = null;
        }
        if (baseIterator.hasNext()) {
            Map.Entry<String, Object> entry = baseIterator.next();
            if (entry.getValue() instanceof TreeMap) {
                currentPrefix = entry.getKey();
                subIterator = new TreeIterator((TreeMap<String, Object>) entry.getValue());
                return hasNext();
            }
            nextIndex = entry.getKey();
            next = entry.getValue();
            return true;
        }
        return false;
    }

    @Override
    public Object next() {
        Object tempcurrent = next;
        currentIndex = nextIndex;
        next = null;
        nextIndex = null;
        return tempcurrent;
    }

    public String currentIndex() {
        return currentIndex;
    }
}
