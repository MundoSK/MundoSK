package com.pie.tlatoani.Throwable;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class OldExprPropertyNameOfSTE extends SimpleExpression<String>{
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
	public String toString(@Nullable Event event, boolean arg1) {
		String property;
		switch (mark) {
            case 0: property = "class"; break;
            case 1: property = "file"; break;
            case 2: property = "method"; break;
            default: throw new IllegalStateException("Mark = " + mark + ", should be 0, 1, 2");
        }
		return property + " name of " + ste;
	}

	@Override
	@Nullable
	protected String[] get(Event event) {
		String result = null;
		StackTraceElement elem = ste.getSingle(event);
		if (mark == 0) result = elem.getClassName();
		if (mark == 1) result = elem.getFileName();
		if (mark == 2) result = elem.getMethodName();
		return new String[]{result};
	}

}