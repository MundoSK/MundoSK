package com.pie.tlatoani.Util.Collections;

import com.google.common.collect.Streams;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Tlatoani on 3/21/18.
 */
public interface Streamable<T> extends Iterable<T> {

    default Stream<T> stream() {
        return Streams.stream(this);
    }

    static <T> Streamable<T> empty() {
        return of(Collections.<T>emptyList());
    }

    static <T> Streamable<T> singleton(T element) {
        return of(Collections.singleton(element));
    }

    static <T> Streamable<T> of(T[] array) {
        return of(Arrays.asList(array));
    }

    static <T> Streamable<T> of(Collection<T> collection) {
        return new StreamableCollection<>(collection);
    }

    class StreamableCollection<T> implements Streamable<T> {
        private final Collection<T> collection;

        private StreamableCollection(Collection<T> collection) {
            this.collection = collection;
        }

        @Override
        public Iterator<T> iterator() {
            return collection.iterator();
        }

        @Override
        public Stream<T> stream() {
            return collection.stream();
        }
    }
}
