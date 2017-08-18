package com.pie.tlatoani.WorldManagement.WorldLoader;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import org.bukkit.World;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/28/17.
 */
public class EffLoadWorldAutomatically extends Effect {
    private Expression<World> worldExpression;
    private boolean load;

    @Override
    protected void execute(Event event) {
        World world = worldExpression.getSingle(event);
        if (load) {
            WorldLoader.setCreator(WorldCreatorData.fromWorld(world));
        } else {
            WorldLoader.removeCreator(world.getName());
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return (load ? "" : "don't ") + "load " + worldExpression + " automatically";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        worldExpression = (Expression<World>) expressions[0];
        load = parseResult.mark == 0;
        return true;
    }
}
