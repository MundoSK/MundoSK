package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 2/25/17.
 */
public class ExprChunkBlock extends SimpleExpression<Block> {
    private Expression<Chunk> chunkExpression;

    private Expression<Number> xExpr = null;
    private Expression<Number> yExpr;
    private Expression<Number> zExpr;

    private int level;
    public static final int LAYER = 0;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int SEA_LEVEL = 3;
    private boolean south;
    private boolean east;
    private boolean center;

    public static int NS = 0b00100;
    public static int EW = 0b01000;
    public static int CORCEN = 0b10000;

    public static int getLevel(int level, Expression<Number> expression, Event event) {
        switch (level) {
            case LAYER: return expression.getSingle(event).intValue();
            case TOP: return 255;
            case BOTTOM: return 0;
            case SEA_LEVEL: return 63;
        }
        throw new IllegalArgumentException("level = " + level + " is not 0, 1, 2, 3");
    }

    public static String levelString(int level, Expression<Number> expression) {
        switch (level) {
            case LAYER: return "layer " + expression;
            case TOP: return "top";
            case BOTTOM: return "bottom";
            case SEA_LEVEL: return "sea level";
        }
        throw new IllegalArgumentException("level = " + level + " is not 0, 1, 2, 3");
    }

    @Override
    protected Block[] get(Event event) {
        int x;
        int y;
        int z;
        if (xExpr != null) {
            x = xExpr.getSingle(event).intValue();
            y = yExpr.getSingle(event).intValue();
            z = zExpr.getSingle(event).intValue();
        } else {
            y = getLevel(level, yExpr, event);
            if (center) {
                x = east ? 8 : 7;
                z = south ? 8 : 7;
            } else {
                x = east ? 15 : 0;
                z = south ? 15 : 0;
            }
        }
        return new Block[]{chunkExpression.getSingle(event).getBlock(x, y, z)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        if (xExpr != null) {
            return "block at " + xExpr + ", " + yExpr + ", " + zExpr + " in " + chunkExpression;
        } else {
            return levelString(level, yExpr) + " " +(south ? "south" : "north") + (east ? "east " : "west") + " " + (center ? "center" : "corner") + " of " + chunkExpression;
        }
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (i == 0) {
            xExpr = (Expression<Number>) expressions[0];
            yExpr = (Expression<Number>) expressions[1];
            zExpr = (Expression<Number>) expressions[2];
            chunkExpression = (Expression<Chunk>) expressions[3];
        } else {
            int mark = parseResult.mark;
            yExpr = (Expression<Number>) expressions[0];
            level = mark % 4;
            south = (mark & NS) == 0;
            east = (mark & EW) == 0;
            center = (mark & CORCEN) == 0;
            chunkExpression = (Expression<Chunk>) expressions[1];
        }
        return true;
    }
}
