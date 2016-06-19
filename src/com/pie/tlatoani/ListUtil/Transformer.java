package com.pie.tlatoani.ListUtil;

import ch.njol.skript.lang.Expression;
import org.bukkit.event.Event;

import java.lang.reflect.Array;

/**
 * Created by Tlatoani on 6/15/16.
 */
public interface Transformer<T> {

    Boolean init(Expression expression);

    Class<? extends T> getType();

    boolean isSettable();

    T[] get(Event event);

    default void setSafely(Event event, Object[] value) {
        T[] result = (T[]) Array.newInstance(getType(), value.length);
        for (int i = 0; i < value.length; i++) {
            result[i] = (T) value[i];
        }
        set(event, result);
    }

    void set(Event event, T[] value);

    interface Addable<T, U> extends Transformer<T> {

        T add(T orig, U addend);

        Class<? extends U> getAddendType();

    }

    interface Resettable<T> extends Transformer<T> {

        T reset();

    }

    interface Removeable<T, U> extends Transformer<T> {

        T remove(T orig, U subtrahend);

        Class<? extends U> getSubtrahendType();

    }
}
