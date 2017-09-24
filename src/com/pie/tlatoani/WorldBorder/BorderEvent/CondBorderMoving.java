package com.pie.tlatoani.WorldBorder.BorderEvent;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/16/17.
 */
public class CondBorderMoving extends SimpleExpression<Boolean> {
    private Expression<World> worldExpression;
    private boolean moving;

    @Override
    protected Boolean[] get(Event event) {
        World world = worldExpression.getSingle(event);
        if (world.getWorldBorder() instanceof WorldBorderImpl) {
            WorldBorderImpl border = (WorldBorderImpl) world.getWorldBorder();
            return new Boolean[]{moving == border.isMoving()};
        }
        return new Boolean[0];
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
        return "border of " + worldExpression + " is " + (moving ? "moving" : "stable");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        worldExpression = (Expression<World>) expressions[0];
        moving = parseResult.mark == 0;
        return true;
    }
}
