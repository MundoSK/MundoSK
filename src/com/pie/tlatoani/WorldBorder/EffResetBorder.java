package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffResetBorder extends Effect{
	private Expression<World> worldExpression;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		worldExpression = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "reset";
	}

	@Override
	protected void execute(Event event) {
		worldExpression.getSingle(event).getWorldBorder().reset();
		
	}

}