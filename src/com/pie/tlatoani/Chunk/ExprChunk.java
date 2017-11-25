package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.google.common.collect.Iterators;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Tlatoani on 2/25/17.
 * Note: If the two locations in the from %location% to %location% alias have different worlds, no chunks are returned
 */
public class ExprChunk extends SimpleExpression<Chunk> {
    private Expression<Number> x1Expr;
    private Expression<Number> z1Expr;
    private Expression<Number> x2Expr;
    private Expression<Number> z2Expr;
    private Expression<World> worldExpression;

    private Expression<Location> loc1Expr;
    private Expression<Location> loc2Expr;

    private boolean single;
    private boolean coords;

    @Override
    protected Chunk[] get(Event event) {
        int x1;
        int z1;
        World world;
        if (coords) {
            x1 = x1Expr.getSingle(event).intValue();
            z1 = z1Expr.getSingle(event).intValue();
            world = worldExpression.getSingle(event);
        } else {
            Location loc1 = loc1Expr.getSingle(event);
            x1 = loc1.getBlockX() >> 4;
            z1 = loc1.getBlockZ() >> 4;
            world = loc1.getWorld();
        }
        if (single) {
            return new Chunk[]{world.getChunkAt(x1, z1)};
        } else {
            int x2;
            int z2;
            if (coords) {
                x2 = x2Expr.getSingle(event).intValue();
                z2 = z2Expr.getSingle(event).intValue();
            } else {
                Location loc2 = loc2Expr.getSingle(event);
                if (!world.equals(loc2.getWorld())) {
                    return new Chunk[0];
                }
                x2 = loc2.getBlockX() >> 4;
                z2 = loc2.getBlockZ() >> 4;
            }
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
        World world;
        int x1;
        int z1;
        int x2;
        int z2;
        if (coords) {
            world = worldExpression.getSingle(event);
            x1 = x1Expr.getSingle(event).intValue();
            z1 = z1Expr.getSingle(event).intValue();
            x2 = x2Expr.getSingle(event).intValue();
            z2 = z2Expr.getSingle(event).intValue();
        } else {
            Location loc1 = loc1Expr.getSingle(event);
            Location loc2 = loc2Expr.getSingle(event);
            if (!loc1.getWorld().equals(loc2.getWorld())) {
                return null;
            }
            world = loc1.getWorld();
            x1 = loc1.getBlockX() >> 4;
            z1 = loc1.getBlockZ() >> 4;
            x2 = loc2.getBlockX() >> 4;
            z2 = loc2.getBlockZ() >> 4;

        }
        int xmin = Math.min(x1, x2);
        int xmax = Math.max(x1, x2);
        int zmin = Math.min(z1, z2);
        int zmax = Math.max(z1, z2);
        return new Iterator<Chunk>() {
            int x = xmin;
            int z = zmin;

            @Override
            public boolean hasNext() {
                return x <= xmax;
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
        if (coords) {
            if (single) {
                return "chunk " + x1Expr + ", " + z1Expr + " in " + worldExpression;
            } else {
                return "chunks from " + x1Expr + ", " + z1Expr + " to " + x2Expr + ", " + z2Expr + " in " + worldExpression;
            }
        } else {
            if (single) {
                return "chunk at " + loc1Expr;
            } else {
                return "chunks from " + loc1Expr + " to " + loc2Expr;
            }
        }

    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        single = (i % 2) == 0;
        coords = i < 2;
        if (coords) {
            x1Expr = (Expression<Number>) expressions[0];
            z1Expr = (Expression<Number>) expressions[1];
            if (single) {
                worldExpression = (Expression<World>) expressions[2];
            } else {
                x2Expr = (Expression<Number>) expressions[2];
                z2Expr = (Expression<Number>) expressions[3];
                worldExpression = (Expression<World>) expressions[4];
            }
        } else {
            loc1Expr = (Expression<Location>) expressions[0];
            if (!single) {
                loc2Expr = (Expression<Location>) expressions[1];
            }
        }

        return true;
    }
}
