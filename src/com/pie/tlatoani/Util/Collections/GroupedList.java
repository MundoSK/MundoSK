package com.pie.tlatoani.Util.Collections;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Tlatoani on 11/26/17.
 */
public class GroupedList<E> {
    private List<E> list = new ArrayList<E>();
    private Optional<Key> last = Optional.empty();

    public E get(int index) {
        return list.get(index);
    }

    public Stream<E> stream() {
        return list.stream();
    }

    public void clear() {
        last = Optional.empty();
        list.clear();
    }

    public Key addGroup(Collection<E> elems) {
        return new Key(elems);
    }

    public void removeGroup(Key key) {
        key.remove();
    }

    public class Key {
        public final int amount;
        private int start;
        private Optional<Key> prev;
        private Optional<Key> next = Optional.empty();

        private Key(Collection<E> elems) {
            this.amount = elems.size();
            list.addAll(elems);
            this.prev = last;
            last = Optional.of(this);
            this.start = prev.map(key -> key.start + key.amount).orElse(0);
            prev.ifPresent(key -> key.next = Optional.of(Key.this));
        }

        private void remove() {
            for (int i = 0; i < amount; i++) {
                list.remove(start);
            }
            for (Key key = next.orElse(null); key != null; key = key.next.orElse(null)) {
                key.start -= amount;
            }
            prev.ifPresent(key -> key.next = next);
            next.ifPresent(key -> key.prev = prev);
            if (!next.isPresent()) {
                last = prev;
            }
        }
    }
}
