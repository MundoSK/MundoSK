package com.pie.tlatoani.Util.Collections;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class CircularIterator<E> implements ListIterator<E> {
    private ArrayList<E> list;
    private Integer index = 0;

    public CircularIterator(ArrayList<E> list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public E next() {
        index = nextIndex();
        return list.get(index);
    }

    @Override
    public boolean hasPrevious() {
        return true;
    }

    @Override
    public E previous() {
        index = previousIndex();
        return list.get(index);
    }

    @Override
    public int nextIndex() {
        return (index + 1) % list.size();
    }

    @Override
    public int previousIndex() {
        return (index + list.size() - 1) % list.size();
    }

    @Override
    public void remove() {
        list.remove(index);
    }

    @Override
    public void set(E o) {
        list.set(index, o);
    }

    @Override
    public void add(E o) {
        list.add(index, o);
    }

}
