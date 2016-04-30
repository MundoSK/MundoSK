package com.pie.tlatoani.Throwable;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprCatch extends SimpleExpression<Throwable>{
	static WeakHashMap<Event, Throwable> catches = new WeakHashMap<Event, Throwable>();

	@Override
	public Class<? extends Throwable> getReturnType() {
		return Throwable.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "catch";
	}

	@Override
	@Nullable
	protected Throwable[] get(Event arg0) {
		Throwable result = null;
		if (catches.containsKey(arg0)) result = catches.get(arg0);
		return new Throwable[]{result};
	}

}