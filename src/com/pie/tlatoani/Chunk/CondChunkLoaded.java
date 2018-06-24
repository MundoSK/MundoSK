package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.Chunk;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 3/6/17.
 */
public class CondChunkLoaded extends SimpleExpression<Boolean> {
    private Expression<Chunk> chunkExpression;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{Utilities.check(chunkExpression, event, Chunk::isLoaded, positive)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return chunkExpression + " " + (positive ? "is" : "isn't") + " loaded";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        chunkExpression = (Expression<Chunk>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }
}
