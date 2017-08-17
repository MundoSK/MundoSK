package com.pie.tlatoani.WorldManagement;

import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffUnloadWorld extends Effect{
	private Expression<World> world;
	private Expression<Boolean> save;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		// TODO Auto-generated method stub
		world = (Expression<World>) expr[0];
		save = (Expression<Boolean>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " setSafely world border of world";
	}

	@Override
	protected void execute(Event event) {
		Boolean boo = true;
		if (save != null) {
			boo = save.getSingle(event);
		}
		Bukkit.getServer().unloadWorld(world.getSingle(event), boo);
		
		
	}

}