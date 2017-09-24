package com.pie.tlatoani.TerrainControl;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.khorn.terraincontrol.TerrainControl;
import org.bukkit.Location;
import org.bukkit.event.Event;

public class ExprBiomeAt extends SimpleExpression<String>{
	private Expression<Location> loc;

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
		loc = (Expression<Location>) expr[0];
		return true;
	}

	@Override
	public String toString(Event event, boolean arg1) {
		return "tc biome at " + loc;
	}

	@Override
	protected String[] get(Event event) {
		int x = loc.getSingle(event).getBlockX();
		int z = loc.getSingle(event).getBlockZ();
		String w = loc.getSingle(event).getWorld().getName();
		return new String[]{TerrainControl.getBiomeName(w, x, z)};
	}

}