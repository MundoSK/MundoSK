package com.pie.tlatoani.WorldBorder.BorderEvent;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.event.Event;

public class ExprTimeRemainingUntilBorderStabilize extends SimpleExpression<Timespan>{
	private Expression<World> worldExpression;

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		worldExpression = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(Event event, boolean arg1) {
		return "remaining time until border stabilize in " + worldExpression;
	}

	@Override
	protected Timespan[] get(Event event) {
		World world = worldExpression.getSingle(event);
		if (world.getWorldBorder() instanceof WorldBorderImpl) {
		    WorldBorderImpl border = (WorldBorderImpl) world.getWorldBorder();
		    Double timeInSeconds = border.remainingTimeInSeconds();
		    Timespan result = new Timespan((long) (timeInSeconds * 1000));
		    return new Timespan[]{result};
        }
		return new Timespan[0];
	}

}