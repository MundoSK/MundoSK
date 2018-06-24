package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.Chunk;
import org.bukkit.event.Event;

import java.util.Random;

/**
 * Created by Tlatoani on 2/25/17.
 */
public class CondSlimey extends SimpleExpression<Boolean> {
    private Expression<Chunk> chunkExpression;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{Utilities.check(chunkExpression, event, chunk -> {
            //Source for following formula is MinecraftWiki http://minecraft.gamepedia.com/Slime#.22Slime_chunks.22
            Random random = new Random(chunk.getWorld().getSeed() +
                    (long) (chunk.getX() * chunk.getX() * 0x4c1906) +
                    (long) (chunk.getX() * 0x5ac0db) +
                    (long) (chunk.getZ() * chunk.getZ()) * 0x4307a7L +
                    (long) (chunk.getZ() * 0x5f24f) ^ 0x3ad8025f);
            return random.nextInt(10) == 0;
        }, positive)};
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
        return chunkExpression + " " + (positive ? "is" : "isn't") + " slimey";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        chunkExpression = (Expression<Chunk>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }
}
