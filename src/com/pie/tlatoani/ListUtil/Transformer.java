package com.pie.tlatoani.ListUtil;

import ch.njol.skript.lang.Expression;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.util.function.Function;

/**
 * Created by Tlatoani on 6/15/16.
 */
public interface Transformer<T> {

    boolean init(Expression expression);

    Class<? extends T> getType();

    boolean isSettable();

    //The array returned by this method should NEVER be modified
    T[] get(Event event);

    default T[] createArray(int length) {
        return (T[]) Array.newInstance(getType(), length);
    }

    void set(Event event, Function<Object[], Object[]> changer);

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
