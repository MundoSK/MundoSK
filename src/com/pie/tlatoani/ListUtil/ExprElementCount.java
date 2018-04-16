package com.pie.tlatoani.ListUtil;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/11/16.
 */
public class ExprElementCount extends SimpleExpression<Number> {
    private String pattern;
    private Transformer<?> transformer;
    private Expression expression;
    private Boolean isSettable;
    private Boolean usedAsLast;

    //For Skript usage
    public ExprElementCount() {}

    //For use as last index
    public ExprElementCount(Transformer transformer, Expression expression) {
        usedAsLast = true;
        this.transformer = transformer;
        this.expression = expression;
        pattern = null;
        isSettable = false;
    }

    @Override
    protected Number[] get(Event event) {
        return new Number[]{transformer.get(event).length};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return usedAsLast ? "last" : pattern + " count of " + expression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        usedAsLast = false;
        expression = expressions[0];
        pattern = ListUtil.getApplicablePattern(i);
        transformer = ListUtil.getTransformer(i, expression);
        if (transformer == null) {
            return false;
        }
        isSettable = transformer.isSettable();
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        transformer.set(event, original -> {
            Integer newcount = 0;
            if (mode == Changer.ChangeMode.SET) {
                newcount = ((Number) delta[0]).intValue();
            } else if (mode == Changer.ChangeMode.ADD) {
                newcount = original.length + ((Number) delta[0]).intValue();
            } else if (mode == Changer.ChangeMode.REMOVE) {
                newcount = original.length - ((Number) delta[0]).intValue();
            }
            if (newcount < 0) {
                newcount = 0;
            }
            Object[] finalarray = transformer.createArray(newcount);
            if (newcount <= original.length) {
                System.arraycopy(original, 0, finalarray, 0, newcount);
            } else {
                System.arraycopy(original, 0, finalarray, 0, original.length);
                if (transformer instanceof Transformer.Resettable) {
                    for (int i = original.length; i < newcount; i++) {
                        finalarray[i] = ((Transformer.Resettable) transformer).reset();
                    }
                } else {
                    for (int i = original.length; i < newcount; i++) {
                        finalarray[i] = null;
                    }
                }
            }
            return finalarray;
        });
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (!isSettable) return null;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) return CollectionUtils.array(Number.class);
        if (mode == Changer.ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }
}
