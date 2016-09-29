package com.pie.tlatoani.Util;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffScope extends Effect{

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		Skript.error("The '$ scope' effect is no longer needed to activate custom scopes, and will be removed in a future version, so it is recommended that you remove it as soon as possible");
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "$ scope";
	}

	@Override
	protected void execute(Event arg0) {}

}