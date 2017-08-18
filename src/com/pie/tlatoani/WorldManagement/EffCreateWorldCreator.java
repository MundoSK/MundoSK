package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import org.bukkit.event.Event;

public class EffCreateWorldCreator extends Effect{
	private Expression<WorldCreatorData> creator;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		creator = (Expression<WorldCreatorData>) expr[0];
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return "create new world using " + creator;
	}

	@Override
	protected void execute(Event event) {
		creator.getSingle(event).createWorld();
		
		

	}

}