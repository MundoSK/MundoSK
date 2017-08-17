package com.pie.tlatoani.Throwable;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprLineNumberOfSTE extends SimpleExpression<Integer>{
	private Expression<StackTraceElement> ste;

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		ste = (Expression<StackTraceElement>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return "line number of " + ste;
	}

	@Override
	@Nullable
	protected Integer[] get(Event event) {
		return new Integer[]{ste.getSingle(event).getLineNumber()};
	}

}