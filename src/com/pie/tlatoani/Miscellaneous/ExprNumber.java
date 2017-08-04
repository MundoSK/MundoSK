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
    private static String[] numberTypes = {"byte", "int", "double", "float", "short", "long"};

    public static Number getValue(Number number, String numberType) {
        switch (numberType) {
            case "byte": return number.byteValue();
            case "int": return number.intValue();
            case "double": return number.doubleValue();
            case "float": return number.floatValue();
            case "short": return number.shortValue();
            case "long": return number.longValue();
        }
        throw new IllegalArgumentException("Illegal numberType: " + numberType);
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
        String numberType = numberTypes[index];
        Number preValue = ((Literal<Number>) expressions[0]).getSingle();
        value = getValue(preValue, numberType);
        toString = preValue + " " + numberType;
        return false;
    }
}
