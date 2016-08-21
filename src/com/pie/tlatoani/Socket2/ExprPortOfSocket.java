package com.pie.tlatoani.Socket2;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/19/16.
 */
public class ExprPortOfSocket extends SimpleExpression<Number> {
    private Expression<Socket2> socket2Expression;
    private boolean isExternal;

    @Override
    protected Number[] get(Event event) {
        return new Number[]{isExternal ? socket2Expression.getSingle(event).getExternalPort() : socket2Expression.getSingle(event).getLocalPort()};
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
        return socket2Expression + "'s " + (isExternal ? "external" : "local") + " host";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        socket2Expression = (Expression<Socket2>) expressions[0];
        isExternal = i < 2;
        return true;
    }
}
