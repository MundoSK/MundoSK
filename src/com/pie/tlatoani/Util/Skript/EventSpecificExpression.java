package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 9/3/17.
 */
public abstract class EventSpecificExpression<T, E extends Event> extends SimpleExpression<T> {
    private static final Map<Class<? extends EventSpecificExpression>, Info> infos = new HashMap<>();

    public static void registerEventSpecificExpression(Class<? extends EventSpecificExpression> expressionClass, Class<?> returnType, Class<? extends Event> eventClass, String toString, String invalidEventError) {
        infos.put(expressionClass, new Info(returnType, eventClass, toString, invalidEventError));
    }

    protected Info info;

    public static class Info {
        public final Class<?> returnType;
        public final Class<? extends Event> eventClass;
        public final String toString;
        public final String invalidEventError;

        public Info(Class<?> returnType, Class<? extends Event> eventClass, String toString, String invalidEventError) {
            this.returnType = returnType;
            this.eventClass = eventClass;
            this.toString = toString;
            this.invalidEventError = invalidEventError;
        }
    }

    public static abstract class Single<T, E extends Event> extends EventSpecificExpression<T, E> {

        protected abstract T getValue(E event);

        @Override
        protected T[] get(Event event) {
            if (info.eventClass.isInstance(event)) {
                T[] result = (T[]) Array.newInstance(info.returnType, 1);
                result[0] = getValue((E) event);
                return result;
            }
            return (T[]) Array.newInstance(info.returnType, 0);
        }

        @Override
        public boolean isSingle() {
            return true;
        }
    }

    public static abstract class Plural<T, E extends Event> extends EventSpecificExpression<T, E> {

        protected abstract T[] getValue(E event);

        @Override
        protected T[] get(Event event) {
            if (info.eventClass.isInstance(event)) {
                return getValue((E) event);
            }
            return (T[]) Array.newInstance(info.returnType, 0);
        }

        @Override
        public boolean isSingle() {
            return false;
        }
    }

    private EventSpecificExpression() {}

    @Override
    protected abstract T[] get(Event event);

    @Override
    public abstract boolean isSingle();

    @Override
    public Class<? extends T> getReturnType() {
        return (Class<? extends T>) info.returnType;
    }

    @Override
    public String toString(Event event, boolean b) {
        return info.toString;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        info = infos.get(getClass());
        if (!Utilities.posCurrentEvent(info.eventClass)) {
            Skript.error(info.invalidEventError);
            return false;
        }
        return true;
    }
}
