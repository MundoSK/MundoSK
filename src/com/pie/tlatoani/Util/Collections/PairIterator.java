package com.pie.tlatoani.Util.Collections;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by Tlatoani on 12/20/17.
 */
public class PairIterator<K, V> implements Iterator<V> {
    private final Iterator<? extends Map.Entry<K, V>> pairIterator;
    private Optional<K> currentKey = Optional.empty();

    public PairIterator(Iterator<? extends Map.Entry<K, V>> pairIterator) {
        this.pairIterator = pairIterator;
    }

    @Override
    public boolean hasNext() {
        return pairIterator.hasNext();
    }

    @Override
    public V next() {
        Map.Entry<K, V> nextPair = pairIterator.next();
        currentKey = Optional.of(nextPair.getKey());
        return nextPair.getValue();
    }

    public K key() {
        if (!currentKey.isPresent()) {
            throw new NoSuchElementException();
        }
        return currentKey.get();
    }
}
