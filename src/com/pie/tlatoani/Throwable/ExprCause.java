package com.pie.tlatoani.Throwable;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprCause extends SimpleExpression<Throwable>{
	private Expression<Throwable> thr;

	@Override
	public Class<? extends Throwable> getReturnType() {
		return Throwable.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		thr = (Expression<Throwable>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "thrwoable cause of " + thr;
	}

	@Override
	@Nullable
	protected Throwable[] get(Event arg0) {
		return new Throwable[]{thr.getSingle(arg0).getCause()};
	}

}