package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprFinalSizeOfBorder extends SimpleExpression<Double>{
	private Expression<World> border;

	@Override
	public Class<? extends Double> getReturnType() {
		return Double.class;
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
	protected Double[] get(Event arg0) {
		Double result = UtilBorderManager.getStableSize(border.getSingle(arg0));
		if (result == null) result = border.getSingle(arg0).getWorldBorder().getSize();
		return new Double[]{result};
	}

}