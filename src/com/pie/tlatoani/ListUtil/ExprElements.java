package com.pie.tlatoani.ListUtil;

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
 * Created by Tlatoani on 6/10/16.
 */
public class ExprElements extends SimpleExpression<Object> implements ListUtil.TransformerUser {
    private String pattern;
    private Transformer transformer;
    private Expression expression;
    private Class returnType;
    private Class typeToSetTo;
    private Boolean isSettable;
    private Boolean isResettable;

    @Override
    protected Object[] get(Event event) {
        return transformer.get(event);
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
        return pattern + "s of " + expression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        expression = expressions[0];
        pattern = ListUtil.retrievePattern(i);
        transformer = ListUtil.retrieveTransformer(pattern, expression);
        if (transformer == null) {
            return false;
        }
        returnType = transformer.getType();
        typeToSetTo = (Array.newInstance(returnType, 0)).getClass();
        isSettable = transformer.isSettable();
        isResettable = transformer instanceof Transformer.Resettable;
        return true;
    }

    public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.SET) {
            transformer.setSafely(arg0, delta);
        } else if (mode == Changer.ChangeMode.ADD) {
            Object[] original = transformer.get(arg0);
            Object[] sum = new Object[original.length + delta.length];
            System.arraycopy(original, 0, sum, 0, original.length);
            System.arraycopy(delta, 0, sum, original.length, delta.length);
            transformer.setSafely(arg0, sum);
        } else if (mode == Changer.ChangeMode.DELETE) {
            transformer.setSafely(arg0, new Object[0]);
        } else if (mode == Changer.ChangeMode.RESET) {
            Object[] original = transformer.get(arg0);
            for (int i = 0; i < original.length; i++) {
                original[i] = ((Transformer.Resettable) transformer).reset();
            }
            transformer.setSafely(arg0, original);
        } else if (mode == Changer.ChangeMode.REMOVE) {
            List original = new ArrayList(Arrays.asList(transformer.get(arg0)));
            for (int i = 0; i < delta.length; i++) {
                original.remove(delta[i]);
            }
            transformer.setSafely(arg0, original.toArray());
        } else if (mode == Changer.ChangeMode.REMOVE_ALL) {
            Object[] original = transformer.get(arg0);
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
            transformer.setSafely(arg0, result);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (!isSettable) return null;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.REMOVE_ALL) return CollectionUtils.array(typeToSetTo);
        if (mode == Changer.ChangeMode.RESET && isResettable) return CollectionUtils.array();
        return null;
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }
}
