package com.pie.tlatoani.ListUtil;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/10/16.
 */
public class ExprElement extends SimpleExpression<Object> implements EffMoveElements.Moveable {
    private String pattern;
    private Transformer<?> transformer;
    private Expression expression;
    private Expression<Number> index;
    private Class returnType;
    private Boolean isSettable;
    private Boolean isResettable;
    private Class typeToAdd;
    private Class typeToSubtract;
    private Boolean isLastIndex;

    @Override
    protected Object[] get(Event event) {
        Integer index = this.index.getSingle(event).intValue() - 1;
        Object[] original = transformer.get(event);
        if (index < 0) {
            index = 0;
        }
        return new Object[]{index < original.length ? original[index] : null};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class getReturnType() {
        return returnType;
    }

    @Override
    public String toString(Event event, boolean b) {
        return isLastIndex ? "last " + pattern + " of " + expression : pattern + " " + index + " of " + expression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        expression = expressions[1];
        pattern = ListUtil.getApplicablePattern(i);
        transformer = ListUtil.getTransformer(i, expression);
        isLastIndex = expressions[0] == null;
        index = !isLastIndex ? (Expression<Number>) expressions[0] : new ExprElementCount(transformer, expression);
        if (transformer == null) {
            return false;
        }
        returnType = transformer.getType();
        isSettable = transformer.isSettable();
        isResettable = transformer instanceof Transformer.Resettable;
        typeToAdd = transformer instanceof Transformer.Addable ? ((Transformer.Addable) transformer).getAddendType() : null;
        typeToSubtract = transformer instanceof Transformer.Removeable ? ((Transformer.Removeable) transformer).getSubtrahendType() : null;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        transformer.set(event, original -> {
            Integer index = this.index.getSingle(event).intValue() - 1;
            if (index >= original.length || index < 0) {
                return original;
            } else if (mode == Changer.ChangeMode.DELETE) {
                Object[] finalarray = transformer.createArray(original.length - 1);
                System.arraycopy(original, 0, finalarray, 0, index);
                System.arraycopy(original, index + 1, finalarray, index, original.length - index - 1);
                return finalarray;

            } else {
                Logging.debug(this, "DELTA = " + delta + ", ORIGINAL = " + original);
                Object value;
                if (mode == Changer.ChangeMode.SET) {
                    value = delta[0];
                } else if (mode == Changer.ChangeMode.ADD) {
                    value = ((Transformer.Addable) transformer).add(original[index], delta[0]);
                } else if (mode == Changer.ChangeMode.REMOVE) {
                    value = ((Transformer.Removeable) transformer).remove(original[index], delta[0]);
                } else if (mode == Changer.ChangeMode.RESET) {
                    value = ((Transformer.Resettable) transformer).reset();
                } else {
                    throw new IllegalArgumentException("Illegal ChangeMode: " + mode);
                }
                Object[] finalArray = transformer.createArray(original.length);
                System.arraycopy(original, 0, finalArray, 0, original.length);
                finalArray[index] = value;
                return finalArray;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (!isSettable) return null;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE) return CollectionUtils.array(returnType);
        if (mode == Changer.ChangeMode.RESET && isResettable) return CollectionUtils.array();
        if (mode == Changer.ChangeMode.ADD && typeToAdd != null) return CollectionUtils.array(typeToAdd);
        if (mode == Changer.ChangeMode.REMOVE && typeToSubtract != null) return CollectionUtils.array(typeToSubtract);
        return null;
    }

    @Override
    public void move(Event event, Integer movement) {
        transformer.set(event, original -> {
            Integer index1 = this.index.getSingle(event).intValue() - 1;
            if (index1 >= original.length || index1 < 0) {
                return original;
            }
            Object[] secondarray = new Object[original.length - 1];
            System.arraycopy(original, 0, secondarray, 0, index1);
            System.arraycopy(original, index1 + 1, secondarray, index1, original.length - index1 - 1);
            Object insertion = original[index1];
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
            Object[] result = transformer.createArray(secondarray.length + 1);
            System.arraycopy(secondarray, 0, result, 0, index);
            result[index] = insertion;
            System.arraycopy(secondarray, index, result, index + 1, secondarray.length - index);
            return result;
        });
    }

    @Override
    public Boolean isMoveable() {
        return transformer.isSettable();
    }
}
