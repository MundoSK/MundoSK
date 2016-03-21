package com.pie.tlatoani.TerrainControl;

import org.bukkit.World;

import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprTCEnabled extends SimpleExpression<Boolean>{
	private Expression<World> world;

	@Override
	public Class<? extends Boolean> getReturnType() {
		// TODO Auto-generated method stub
		return Boolean.class;
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
		world = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	protected Boolean[] get(Event arg0) {
		LocalWorld w = TerrainControl.getWorld(world.getSingle(arg0).getName());
		Boolean result = true;
		if (w == null) {
			result = false;
		}
		return new Boolean[]{result};
	}


}