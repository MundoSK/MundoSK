package com.pie.tlatoani.Util;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprIDOfCustomEvent extends SimpleExpression<String>{

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(UtilCustomEvent.class)) {
			Skript.error("Cannot use 'custom event's id' outside of custom events");
			return false;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "custom event's id";
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		return new String[]{arg0 instanceof UtilCustomEvent ? ((UtilCustomEvent) arg0).getID() : null};
	}

}