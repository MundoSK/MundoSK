package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

public class ExprTimeRemainingUntilBorderStabilize extends SimpleExpression<Timespan>{
	private Expression<World> border;

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		border = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "border length of world";
	}

	@Override
	@Nullable
	protected Timespan[] get(Event arg0) {
		Double result = BorderManager.getRemainingTime(border.getSingle(arg0));
		if (result == null) result = 0.0;
		return new Timespan[]{new Timespan(result.longValue()*1000)};
	}

}