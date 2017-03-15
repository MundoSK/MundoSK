package com.pie.tlatoani.WorldBorder;

import com.pie.tlatoani.Mundo;
import org.bukkit.Location;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class CondBeyondBorder extends SimpleExpression<Boolean>{
	private Expression<Location> locationExpression;
	private Boolean within;

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		locationExpression = (Expression<Location>) expr[0];
		within = arg3.mark == 0;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return locationExpression + " is " + (within ? "within" : "beyond") + " border";
	}

	@Override
	protected Boolean[] get(Event event) {
		return new Boolean[]{Mundo.check(locationExpression, event, loc -> {
		    Location center = loc.getWorld().getWorldBorder().getCenter();
		    Double radius = loc.getWorld().getWorldBorder().getSize() / 2;
		    return (Mundo.isInRange(center.getX() - radius, loc.getX(), center.getX() + radius) &&
                    Mundo.isInRange(center.getZ() - radius, loc.getZ(), center.getZ() + radius)) ==
                    within;
        })};
	}


}