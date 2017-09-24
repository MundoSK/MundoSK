package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Tlatoani on 2/25/17.
 */
public class ExprChunkBlocks extends SimpleExpression<Block> {
    private Expression<Number> x1Expression = null;
    private Expression<Number> y1Expression = null;
    private Expression<Number> z1Expression = null;
    private Expression<Number> x2Expression = null;
    private Expression<Number> y2Expression = null;
    private Expression<Number> z2Expression = null;
    private Expression<Chunk> chunkExpression;
    private int matchedPattern;
    private int level1;
    private int level2;

    public static final int ALL_BLOCKS = 0;
    public static final int FROM_TO = 1;
    public static final int SINGLE_LAYER = 2;
    public static final int MULTIPLE_LAYER = 3;
    public static final int ALT_MULTIPLE_LAYER = 4;

    public static Block[] region(Chunk chunk, int x1, int y1, int z1, int x2, int y2, int z2) {
        int xn = 1 + x2 - x1;
        int yn = 1 + y2 - y1;
        int zn = 1 + z2 - z1;
        Block[] blocks = new Block[xn * yn * zn];
        int i = 0;
        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                for (int z = z1; z <= z2; z++) {
                    blocks[i] = chunk.getBlock(x, y, z);
                    i++;
                }
        return blocks;
    }

    public static Iterator<Block> regionIterator(Chunk chunk, int x1, int y1, int z1, int x2, int y2, int z2) {
        return new Iterator<Block>() {
            int x = x1;
            int y = y1;
            int z = z1;

            @Override
            public boolean hasNext() {
                return x <= x2;
            }

            @Override
            public Block next() {
                if (x > x2) {
                    throw new NoSuchElementException();
                }
                Block result = chunk.getBlock(x, y, z);
                if (z < z2) {
                    z++;
                } else if (y < y2) {
                    z = z1;
                    y++;
                } else {
                    z = z1;
                    y = y1;
                    x++;
                }
                return result;
            }
        };
    }

    @Override
    protected Block[] get(Event event) {
        int x1 = 0;
        int y1 = 0;
        int z1 = 0;
        int x2 = 15;
        int y2 = 15;
        int z2 = 15;
        switch (matchedPattern) {
            case FROM_TO:
                x1 = x1Expression.getSingle(event).intValue();
                z1 = z1Expression.getSingle(event).intValue();
                x2 = x2Expression.getSingle(event).intValue();
                z2 = z2Expression.getSingle(event).intValue();
            case MULTIPLE_LAYER:
                y1 = ExprChunkBlock.getLevel(level1, y1Expression, event);
                y2 = ExprChunkBlock.getLevel(level2, y2Expression, event);
                break;
            case SINGLE_LAYER:
                y1 = ExprChunkBlock.getLevel(level1, y1Expression, event);
                y2 = y1;
                break;
        }
        return region(chunkExpression.getSingle(event),
                Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }

    @Override
    public Iterator<Block> iterator(Event event) {
        int x1 = 0;
        int y1 = 0;
        int z1 = 0;
        int x2 = 15;
        int y2 = 15;
        int z2 = 15;
        switch (matchedPattern) {
            case FROM_TO:
                x1 = x1Expression.getSingle(event).intValue();
                z1 = z1Expression.getSingle(event).intValue();
                x2 = x2Expression.getSingle(event).intValue();
                z2 = z2Expression.getSingle(event).intValue();
            case MULTIPLE_LAYER:
                y1 = ExprChunkBlock.getLevel(level1, y1Expression, event);
                y2 = ExprChunkBlock.getLevel(level2, y2Expression, event);
                break;
            case SINGLE_LAYER:
                y1 = ExprChunkBlock.getLevel(level1, y1Expression, event);
                y2 = y1;
                break;
        }
        return regionIterator(chunkExpression.getSingle(event),
                Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        switch (matchedPattern) {
            case ALL_BLOCKS: return "all blocks in " + chunkExpression;
            case FROM_TO: return "blocks from " + x1Expression + ", " + y1Expression + ", " + z1Expression + " to " + x2Expression + ", " + y2Expression + ", " + z2Expression + " in " + chunkExpression;
            case SINGLE_LAYER: return ExprChunkBlock.levelString(level1, y1Expression) + " in " + chunkExpression;
            case MULTIPLE_LAYER: return ExprChunkBlock.levelString(level1, y1Expression) + " to " + ExprChunkBlock.levelString(level2, y2Expression) + " in " + chunkExpression;
            default: throw new IllegalStateException("Cannot be reached");
        }
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        matchedPattern = i;
        if (matchedPattern == ALT_MULTIPLE_LAYER) {
            matchedPattern = MULTIPLE_LAYER;
        }
        switch (matchedPattern) {
            case ALL_BLOCKS:
                chunkExpression = (Expression<Chunk>) expressions[0];
                break;
            case FROM_TO:
                x1Expression = (Expression<Number>) expressions[0];
                y1Expression = (Expression<Number>) expressions[1];
                z1Expression = (Expression<Number>) expressions[2];
                x2Expression = (Expression<Number>) expressions[3];
                y2Expression = (Expression<Number>) expressions[4];
                z2Expression = (Expression<Number>) expressions[5];
                chunkExpression = (Expression<Chunk>) expressions[6];
                break;
            case SINGLE_LAYER:
                level1 = parseResult.mark;
                y1Expression = (Expression<Number>) expressions[0];
                chunkExpression = (Expression<Chunk>) expressions[1];
                break;
            case MULTIPLE_LAYER:
                level1 = parseResult.mark % 4;
                level2 = parseResult.mark >> 2;
                y1Expression = (Expression<Number>) expressions[0];
                y2Expression = (Expression<Number>) expressions[1];
                chunkExpression = (Expression<Chunk>) expressions[2];
        }
        return true;
    }


}
