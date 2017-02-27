package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Tlatoani on 2/25/17.
 */
public class ExprChunk extends SimpleExpression<Chunk> {
    private Expression<Number> x1Expr;
    private Expression<Number> z1Expr;
    private Expression<Number> x2Expr;
    private Expression<Number> z2Expr;
    private Expression<World> worldExpression;

    private boolean single;
    private boolean explicitWorld;

    @Override
    protected Chunk[] get(Event event) {
        int x1 = x1Expr.getSingle(event).intValue();
        int z1 = z1Expr.getSingle(event).intValue();
        World world = worldExpression.getSingle(event);
        if (single) {
            return new Chunk[]{world.getChunkAt(x1, z1)};
        } else {
            int x2 = x2Expr.getSingle(event).intValue();
            int z2 = z2Expr.getSingle(event).intValue();
            int xmin = Math.min(x1, x2);
            int xmax = Math.max(x1, x2);
            int zmin = Math.min(z1, z2);
            int zmax = Math.max(z1, z2);
            int zn = zmax - zmin + 1;
            Chunk[] chunks = new Chunk[(xmax - xmin + 1) * zn];
            for (int x = xmin; x <= xmax; x++)
                for (int z = zmin; z <= zmax; z++) {
                    chunks[(x - xmin) * zn + z - zmin] = world.getChunkAt(x, z);
                }
            return chunks;
        }
    }

    @Override
    public Iterator<Chunk> iterator(Event event) {
        World world = worldExpression.getSingle(event);
        int x1 = x1Expr.getSingle(event).intValue();
        int z1 = z1Expr.getSingle(event).intValue();
        int x2 = x2Expr.getSingle(event).intValue();
        int z2 = z2Expr.getSingle(event).intValue();
        int xmin = Math.min(x1, x2);
        int xmax = Math.max(x1, x2);
        int zmin = Math.min(z1, z2);
        int zmax = Math.max(z1, z2);
        return new Iterator<Chunk>() {
            int x = xmin;
            int z = zmin;

            @Override
            public boolean hasNext() {
                return x > xmax;
            }

            @Override
            public Chunk next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Chunk result = world.getChunkAt(x, z);
                if (z < zmax) {
                    z++;
                } else {
                    z = zmin;
                    x++;
                }
                return result;
            }
        };
    }

    @Override
    public boolean isSingle() {
        return single;
    }

    @Override
    public Class<? extends Chunk> getReturnType() {
        return Chunk.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        if (single) {
            return "chunk " + x1Expr + ", " + z1Expr + (explicitWorld ? " in " + worldExpression : "");
        } else {
            return "chunks from " + x1Expr + ", " + z1Expr + " to " + x2Expr + ", " + z2Expr + (explicitWorld ? " in " + worldExpression : "");
        }
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        single = i == 0;
        x1Expr = (Expression<Number>) expressions[0];
        z1Expr = (Expression<Number>) expressions[1];
        if (single) {
            worldExpression = (Expression<World>) expressions[2];
        } else {
            x2Expr = (Expression<Number>) expressions[2];
            z2Expr = (Expression<Number>) expressions[3];
            worldExpression = (Expression<World>) expressions[4];
        }
        return true;
    }
}
