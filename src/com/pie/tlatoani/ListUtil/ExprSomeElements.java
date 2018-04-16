package com.pie.tlatoani.ListUtil;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

import java.lang.reflect.Array;

/**
 * Created by Tlatoani on 6/11/16.
 */
public class ExprSomeElements extends SimpleExpression<Object> implements EffMoveElements.Moveable {
    private String pattern;
    private Transformer<?> transformer;
    private Expression expression;
    private Class returnType;
    private Class typeToSetTo;
    private Boolean isSettable;
    private Boolean isResettable;
    private Expression<Number> index1;
    private Expression<Number> index2;

    @Override
    protected Object[] get(Event event) {
        Integer index1 = this.index1.getSingle(event).intValue() - 1;
        Integer index2 = this.index2.getSingle(event).intValue() - 1;
        Object[] original = transformer.get(event);
        if (index2 >= original.length) {
            index2 = original.length - 1;
        }
        if (index1 > index2 || index1 < 0) {
            return new Object[0];
        }
        Object[] result = new Object[index2 - index1 + 1];
        System.arraycopy(original, index1, result, 0, index2 - index1 + 1);
        return result;
    }

    public void move(Event event, Integer movement) {
        transformer.set(event, original -> {
            Integer index1 = this.index1.getSingle(event).intValue() - 1;
            Integer index2 = this.index2.getSingle(event).intValue() - 1;
            //Object[] original = transformer.get(event);
            if (index1 > index2 || index2 >= original.length || index1 < 0) {
                return original;
            }
            Object[] secondarray = new Object[original.length + index1 - index2 - 1];
            System.arraycopy(original, 0, secondarray, 0, index1);
            System.arraycopy(original, index2 + 1, secondarray, index1, original.length - index2 - 1);
            Object[] insertion = new Object[index2 - index1 + 1];
            System.arraycopy(original, index1, insertion, 0, index2 - index1 + 1);
            Integer index = index1 + movement;
            if (index < 0) {
                index = 0;
            }
            if (index > secondarray.length) {
                Object[] neworiginal = new Object[index];
                System.arraycopy(secondarray, 0, neworiginal, 0, secondarray.length);
                if (transformer instanceof Transformer.Resettable) {
                    for (int i = secondarray.length; i < index; i++) {
                        neworiginal[i] = ((Transformer.Resettable) transformer).reset();
                    }
                } else {
                    for (int i = secondarray.length; i < index; i++) {
                        neworiginal[i] = null;
                    }
                }
                secondarray = neworiginal;
            }
            Object[] result = transformer.createArray(secondarray.length + insertion.length);
            System.arraycopy(secondarray, 0, result, 0, index);
            System.arraycopy(insertion, 0, result, index, insertion.length);
            System.arraycopy(secondarray, index, result, index + insertion.length, secondarray.length - index);
            //transformer.set(event, result);
            return result;
        });
    }

    public Boolean isMoveable() {
        return transformer.isSettable();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class getReturnType() {
        return returnType;
    }

    @Override
    public String toString(Event event, boolean b) {
        return pattern + "s " + index1 + " to " + index2 + " of " + expression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        expression = expressions[2];
        pattern = ListUtil.getApplicablePattern(i);
        transformer = ListUtil.getTransformer(i, expression);
        if (transformer == null) {
            return false;
        }
        index1 = (Expression<Number>) expressions[0];
        index2 = expressions[1] != null ? (Expression<Number>) expressions[1] : new ExprElementCount(transformer, expression);
        returnType = transformer.getType();
        typeToSetTo = (Array.newInstance(returnType, 0)).getClass();
        isSettable = transformer.isSettable();
        isResettable = transformer instanceof Transformer.Resettable;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        transformer.set(event, original -> {
            Integer index1 = this.index1.getSingle(event).intValue() - 1;
            Integer index2 = this.index2.getSingle(event).intValue() - 1;
            if (index1 > index2 || index2 >= original.length || index1 < 0) {
                return original;
            } else if (mode == Changer.ChangeMode.SET) {
                Object[] finalarray = transformer.createArray(original.length + delta.length + index1 - index2 - 1);
                System.arraycopy(original, 0, finalarray, 0, index1);
                System.arraycopy(delta, 0, finalarray, index1, delta.length);
                System.arraycopy(original, index2 + 1, finalarray, index1 + delta.length, original.length - index2 - 1);
                return finalarray;
            } else if (mode == Changer.ChangeMode.DELETE) {
                Object[] finalarray = transformer.createArray(original.length + index1 - index2 - 1);
                System.arraycopy(original, 0, finalarray, 0, index1);
                System.arraycopy(original, index2 + 1, finalarray, index1, original.length - index2 - 1);
                return finalarray;
            } else if (mode == Changer.ChangeMode.RESET) {
                Object[] finalArray = transformer.createArray(original.length);
                System.arraycopy(original, 0, finalArray, 0, original.length);
                for (int i = index1; i <= index2; i++) {
                    finalArray[i] = ((Transformer.Resettable) transformer).reset();
                }
                return finalArray;
            } else {
                throw new IllegalArgumentException("Illegal ChangeMode: " + mode);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (!isSettable) return null;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE) return CollectionUtils.array(typeToSetTo);
        if (mode == Changer.ChangeMode.RESET && isResettable) return CollectionUtils.array();
        return null;
    }
}
