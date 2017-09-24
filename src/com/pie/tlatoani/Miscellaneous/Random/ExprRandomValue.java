package com.pie.tlatoani.Miscellaneous.Random;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Random;

/**
 * Created by Tlatoani on 7/25/16.
 */
public class ExprRandomValue extends SimpleExpression<Object> {
    Expression<Random> randomExpression;
    Expression<Number> maxExpression;
    int mark; //mark is 0, 1, 2, 3, 4, 5, 6

    private Object getResult(Event event) {
        Random random = randomExpression.getSingle(event);
        switch (mark) {
            case 0: return random.nextInt();
            case 1: return random.nextLong();
            case 2: return random.nextFloat();
            case 3: return random.nextDouble();
            case 4: return random.nextGaussian();
            case 5: return random.nextInt(maxExpression.getSingle(event).intValue());
            default: return random.nextBoolean();
        }
    }

    @Override
    protected Object[] get(Event event) {
        return new Object[]{getResult(event)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        if (mark == 6)
            return Boolean.class;
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "next " + (mark == 0 ? "int" : mark == 1 ? "long" : mark == 2 ? "float" : mark == 3 ? "double" : mark == 4 ? "gaussian" : mark == 5 ? "int less than " + maxExpression : "boolean") + " from random " + randomExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        maxExpression = (Expression<Number>) expressions[0];
        randomExpression = (Expression<Random>) expressions[1];
        mark = parseResult.mark;
        return true;
    }
}
