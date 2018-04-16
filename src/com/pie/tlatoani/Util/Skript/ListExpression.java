package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tlatoani on 7/27/16.
 */
public abstract class ListExpression<T> extends SimpleExpression<T> {
    Class<? extends T> returnType;
    Class typeToSetTo;
    
    @Override
    protected abstract T[] get(Event event);

    @Override
    public abstract Class<? extends T> getReturnType();

    @Override
    public abstract String toString(Event event, boolean b);
    
    public abstract boolean subInit(Expression<?>[] expression, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult);
    
    public abstract boolean isSettable();
    
    public abstract void set(Event event, T[] value);
    
    public abstract T getResettedValue();
    
    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        returnType = getReturnType();
        typeToSetTo = (Array.newInstance(returnType, 0)).getClass();
        return subInit(expressions, i, kleenean, parseResult);
    }
    
    private void setSafely(Event event, Object[] value) {
        T[] result = (T[]) Array.newInstance(getReturnType(), value.length);
        for (int i = 0; i < value.length; i++) {
            result[i] = (T) value[i];
        }
        set(event, result);
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.SET) {
            setSafely(event, delta);
        } else if (mode == Changer.ChangeMode.ADD) {
            Object[] original = get(event);
            Object[] sum = new Object[original.length + delta.length];
            System.arraycopy(original, 0, sum, 0, original.length);
            System.arraycopy(delta, 0, sum, original.length, delta.length);
            setSafely(event, sum);
        } else if (mode == Changer.ChangeMode.DELETE) {
            setSafely(event, new Object[0]);
        } else if (mode == Changer.ChangeMode.RESET) {
            Object[] original = get(event);
            for (int i = 0; i < original.length; i++) {
                original[i] = getResettedValue();
            }
            setSafely(event, original);
        } else if (mode == Changer.ChangeMode.REMOVE) {
            List original = new ArrayList(Arrays.asList(get(event)));
            for (int i = 0; i < delta.length; i++) {
                original.remove(delta[i]);
            }
            setSafely(event, original.toArray());
        } else if (mode == Changer.ChangeMode.REMOVE_ALL) {
            Object[] original = get(event);
            Object[] without = new Object[original.length];
            Integer amountremoved = 0;
            for (int i = 0; i < original.length; i++) {
                Boolean removed = false;
                for (int j = 0; j < delta.length; j++) {
                    if (original[i].equals(delta[j])) {
                        removed = true;
                        amountremoved++;
                        break;
                    }
                }
                if (!removed) {
                    without[i - amountremoved] = original[i];
                }
            }
            Object[] result = new Object[original.length - amountremoved];
            System.arraycopy(without, 0, result, 0, original.length - amountremoved);
            setSafely(event, result);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (!isSettable()) return null;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.REMOVE_ALL) return CollectionUtils.array(typeToSetTo);
        if (mode == Changer.ChangeMode.RESET && getResettedValue() != null) return CollectionUtils.array();
        return null;
    }
}
