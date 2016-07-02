package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.UUID;

/**
 * Created by Tlatoani on 7/1/16.
 */
public class ExprEntityUUID extends SimpleExpression<String> {
    private Expression<Entity> entityExpression;

    @Override
    protected String[] get(Event event) {
        UUID entityUUID = entityExpression.getSingle(event).getUniqueId();
        return new String[]{entityUUID.toString()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "uuid of %entity%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return false;
    }
}
