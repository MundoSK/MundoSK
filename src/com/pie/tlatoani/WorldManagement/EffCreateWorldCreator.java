package com.pie.tlatoani.WorldManagement;

import org.bukkit.WorldCreator;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffCreateWorldCreator extends Effect{
	private Expression<WorldCreator> creator;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		// TODO Auto-generated method stub
		creator = (Expression<WorldCreator>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " setSafely world border of world";
	}

	@Override
	protected void execute(Event event) {
		String n = creator.getSingle(event).name();
		WorldCreator x = new WorldCreator(n);
		x.copy(creator.getSingle(event));
		x.type(creator.getSingle(event).type());
		x.generatorSettings(creator.getSingle(event).generatorSettings());
		x.createWorld();
		
		

	}

}