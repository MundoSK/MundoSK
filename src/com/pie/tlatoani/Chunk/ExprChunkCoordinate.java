package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/28/17.
 */
public class ExprChunkCoordinate extends SimpleExpression<Number> {
    private Expression<Chunk> chunkExpression;
    private boolean x;

    @Override
    protected Number[] get(Event event) {
        Chunk chunk = chunkExpression.getSingle(event);
        return new Number[]{
                x ? chunk.getX() : chunk.getZ()
        };
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
        return "chunk " + (x ? "x" : "z") + " of " + chunkExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        chunkExpression = (Expression<Chunk>) expressions[0];
        x = parseResult.mark == 0;
        return true;
    }
}
