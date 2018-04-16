package com.pie.tlatoani.ListUtil;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.function.Function;

/**
 * Created by Tlatoani on 6/11/16.
 */
public class EffInsertElements extends Effect {
    private String pattern;
    private Transformer<?> transformer;
    private Expression expression;
    private Expression insertion;
    private Expression<Number> index;
    private Boolean isLastIndex;
    private Boolean isAfter;

    @Override
    protected void execute(Event event) {
        transformer.set(event, original -> {
            Object[] insertion = this.insertion.getArray(event);
            Integer index = this.index.getSingle(event).intValue() - (isAfter ? 0 : 1);
            if (index < 0) {
                index = 0;
            }
            if (index > original.length) {
                Object[] neworiginal = new Object[index];
                System.arraycopy(original, 0, neworiginal, 0, original.length);
                if (transformer instanceof Transformer.Resettable) {
                    for (int i = original.length; i < index; i++) {
                        neworiginal[i] = ((Transformer.Resettable) transformer).reset();
                    }
                } else {
                    for (int i = original.length; i < index; i++) {
                        neworiginal[i] = null;
                    }
                }
                original = neworiginal;
            }
            Object[] result = transformer.createArray(original.length + insertion.length);
            System.arraycopy(original, 0, result, 0, index);
            System.arraycopy(insertion, 0, result, index, insertion.length);
            System.arraycopy(original, index, result, index + insertion.length, original.length - index);
            return result;
        });
    }

    @Override
    public String toString(Event event, boolean b) {
        return "insert " + insertion + " " + (isAfter ? "after" : "before") + " " + (isLastIndex ? "last " + pattern : pattern + " " + index) + " of " + expression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        expression = expressions[2];
        insertion = expressions[0];
        isAfter = parseResult.mark == 0;
        pattern = ListUtil.getApplicablePattern(i);
        transformer = ListUtil.getTransformer(i, expression);
        isLastIndex = expressions[1] == null;
        index = !isLastIndex ? (Expression<Number>) expressions[1] : new ExprElementCount(transformer, expression);
        if (transformer == null) {
            return false;
        }
        if (!transformer.isSettable() || !(transformer.getType().isAssignableFrom(insertion.getReturnType()) || insertion.getReturnType().isAssignableFrom(transformer.getType()))) {
            Skript.error("'" + insertion + "' cannot be inserted into '" + pattern + "s of " + expression + "'");
            return false;
        }
        return true;
    }
}
