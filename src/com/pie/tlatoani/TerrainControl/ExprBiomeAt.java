package com.pie.tlatoani.TerrainControl;

import org.bukkit.Location;

import com.khorn.terraincontrol.TerrainControl;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprBiomeAt extends SimpleExpression<String>{
	private Expression<Location> loc;

	@Override
	public Class<? extends String> getReturnType() {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		// TODO Auto-generated method stub
		loc = (Expression<Location>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		int x = loc.getSingle(arg0).getBlockX();
		int z = loc.getSingle(arg0).getBlockZ();
		String w = loc.getSingle(arg0).getWorld().getName();
		return new String[]{TerrainControl.getBiomeName(w, x, z)};
	}

}