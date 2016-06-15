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
 * Created by Tlatoani on 6/10/16.
 */
public class ExprItems extends SimpleExpression implements ListUtil.TransformerUser {
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
        return pattern + " of " + expression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        expression = expressions[0];
        pattern = parseResult.regexes.get(0).group();
        transformer = ListUtil.retrieveTransformerByPlural(pattern, expression);
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
        }
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (!isSettable) return null;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.ADD) return CollectionUtils.array(typeToSetTo);
        if (mode == Changer.ChangeMode.RESET && isResettable) return CollectionUtils.array();
        return null;
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }
}
