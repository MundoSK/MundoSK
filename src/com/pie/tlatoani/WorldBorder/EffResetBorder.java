package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffResetBorder extends Effect{
	private Expression<World> border;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		// TODO Auto-generated method stub
		border = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " set world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		// TODO Auto-generated method stub
		WorldBorder b = border.getSingle(arg0).getWorldBorder();
		b.reset();
		
	}

}