package com.pie.tlatoani.TerrainControl;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import org.bukkit.World;
import org.bukkit.event.Event;

public class CondTCEnabled extends SimpleExpression<Boolean>{
	private Expression<World> world;

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		world = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(Event event, boolean arg1) {
		return "terrain control is enabled for " + world;
	}

	@Override
	protected Boolean[] get(Event event) {
		LocalWorld w = TerrainControl.getWorld(world.getSingle(event).getName());
		Boolean result = true;
		if (w == null) {
			result = false;
		}
		return new Boolean[]{result};
	}


}