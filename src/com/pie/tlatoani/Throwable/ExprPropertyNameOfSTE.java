package com.pie.tlatoani.Throwable;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprPropertyNameOfSTE extends SimpleExpression<String>{
	private Expression<StackTraceElement> ste;
	private Integer mark;

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		ste = (Expression<StackTraceElement>) expr[0];
		mark = arg3.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "(class|file|method) name";
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		String result = null;
		StackTraceElement elem = ste.getSingle(arg0);
		if (mark == 0) result = elem.getClassName();
		if (mark == 1) result = elem.getFileName();
		if (mark == 2) result = elem.getMethodName();
		return new String[]{result};
	}

}