package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/28/17.
 */
public class ExprChunkWorld extends SimpleExpression<World> {
    private Expression<Chunk> chunkExpression;

    @Override
    protected World[] get(Event event) {
        return new World[]{chunkExpression.getSingle(event).getWorld()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends World> getReturnType() {
        return World.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "chunk world of " + chunkExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        chunkExpression = (Expression<Chunk>) expressions[0];
        return true;
    }
}
