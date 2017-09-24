package com.pie.tlatoani.Achievement;

import org.bukkit.Achievement;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprParentAch extends SimpleExpression<Achievement>{
	private Expression<Achievement> ach;

	@Override
	public Class<? extends Achievement> getReturnType() {
		// TODO Auto-generated method stub
		return Achievement.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		// TODO Auto-generated method stub
		ach = (Expression<Achievement>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected Achievement[] get(Event event) {
		return new Achievement[]{ach.getSingle(event).getParent()};
	}

}