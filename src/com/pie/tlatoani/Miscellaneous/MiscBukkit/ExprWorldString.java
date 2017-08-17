package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprWorldString extends SimpleExpression<World>{
	private Expression<String> world;

	@Override
	public Class<? extends World> getReturnType() {
		// TODO Auto-generated method stub
		return World.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		world = (Expression<String>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return "world %string%";
	}

	@Override
	@Nullable
	protected World[] get(Event event) {
		return new World[]{Bukkit.getWorld(world.getSingle(event))};
	}

}