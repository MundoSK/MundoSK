package com.pie.tlatoani.Core.Skript;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tlatoani on 8/18/17.
 */
public abstract class MundoPropertyExpression<F, T> extends SimplePropertyExpression<F, T> {
    private static final Map<Class<? extends MundoPropertyExpression>, Info> infoMap = new HashMap<>();

    protected Info info;
    protected Class returnType;
    protected String property;

    //Allows documentation of changers to work properly
    public MundoPropertyExpression() {
        info = infoMap.get(getClass());
        returnType = info.returnType;
    }

    public static void registerPropertyExpressionInfo(Class<? extends MundoPropertyExpression> exprClass, Class returnType, List<String> properties) {
        infoMap.put(exprClass, new Info(properties.toArray(new String[0]), returnType));
    }

    public static class Info {
        public final String[] properties;
        public final Class returnType;

        public Info(String[] properties, Class returnType) {
            this.properties = properties;
            this.returnType = returnType;
        }
    }

    @Override
    protected String getPropertyName() {
        return property;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        property = info.properties[i];
        return super.init(expressions, i, kleenean, parseResult);
    }

    @Override
    public String toString(Event event, boolean debug) {
        if (property.contains("%")) {
            return property.replace("%", getExpr().toString(event, debug));
        } else {
            return "the " + property + " of " + getExpr().toString(event, debug);
        }
    }

    @Override
    public Class<? extends T> getReturnType() {
        return returnType;
    }

}
