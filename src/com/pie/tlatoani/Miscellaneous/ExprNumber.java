package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/3/17.
 */
public class ExprNumber extends SimpleExpression<Number> {
    private Number value;
    private String toString;
    private static char[] chars = {'b', 'd', 'f', 's', 'l'};

    public static Number getValue(Number number, char ch) {
        switch (ch) {
            case 'b': return number.byteValue();
            case 'd': return number.doubleValue();
            case 'f': return number.floatValue();
            case 's': return number.shortValue();
            case 'l': return number.longValue();
        }
        throw new IllegalArgumentException("Illegal char: " + ch);
    }

    @Override
    protected Number[] get(Event event) {
        return new Number[]{value};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return toString;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        int index = parseResult.mark;
        char ch = chars[index];
        Number preValue = ((Literal<Number>) expressions[0]).getSingle();
        value = getValue(preValue, ch);
        toString = preValue + "" + ch;
        return true;
    }
}
