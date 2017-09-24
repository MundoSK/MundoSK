package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

/**
 * Created by Tlatoani on 10/16/16.
 */
public class ExprServerPort extends SimpleExpression<Number> {

    @Override
    @Nullable
    protected Number[] get(Event e) {
        return new Number[]{Bukkit.getPort()};
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] e, int i, Kleenean k, SkriptParser.ParseResult p) {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean b) {
        return "the server's port";
    }
}