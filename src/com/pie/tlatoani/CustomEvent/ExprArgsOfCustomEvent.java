package com.pie.tlatoani.CustomEvent;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprArgsOfCustomEvent extends SimpleExpression<Object>{

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(SkriptCustomEvent.class))  {
			Skript.error("Cannot use 'custom event's args' outside of custom events");
			return false;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return "custom event's args";
	}

	@Override
	@Nullable
	protected Object[] get(Event event) {
		return event instanceof SkriptCustomEvent ? ((SkriptCustomEvent) event).getArgs() : null;
	}

}