package com.pie.tlatoani.Throwable;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprStackTrace extends SimpleExpression<StackTraceElement>{
	private Expression<Throwable> thr;

	@Override
	public Class<? extends StackTraceElement> getReturnType() {
		return StackTraceElement.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		thr = (Expression<Throwable>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return "stack trace of " + thr;
	}

	@Override
	@Nullable
	protected StackTraceElement[] get(Event event) {
		return thr.getSingle(event).getStackTrace();
	}
	
	public Iterator<StackTraceElement> iterator(Event event) {
		return Arrays.asList(thr.getSingle(event).getStackTrace()).iterator();
	}

}