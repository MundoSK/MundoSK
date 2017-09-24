package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/23/17.
 *
 * An alternative for the built-in Skript effect as that one disallows mounting of multiple entities in a vehicle
 */
public class EffMountVehicle extends Effect {
    private Expression<Entity> passengers;
    private Expression<Entity> vehicle;

    @Override
    protected void execute(Event event) {
        Entity[] passengers = this.passengers.getArray(event);
        Entity vehicle = this.vehicle.getSingle(event);
        for (Entity passenger : passengers) {
            vehicle.addPassenger(passenger);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "mount " + passengers + " on " + vehicle;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        passengers = (Expression<Entity>) expressions[0];
        vehicle = (Expression<Entity>) expressions[1];
        return true;
    }
}
