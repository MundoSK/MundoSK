package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 2/25/17.
 */
public class EffLoadChunk extends Effect {
    private Expression<Chunk> chunkExpression;
    private boolean load;

    @Override
    protected void execute(Event event) {
        if (load) {
            chunkExpression.getSingle(event).load();
        } else {
            chunkExpression.getSingle(event).unload();
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return (load ? "load" : "unload") + " chunk " + chunkExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        chunkExpression = (Expression<Chunk>) expressions[0];
        load = parseResult.mark == 0;
        return true;
    }
}
