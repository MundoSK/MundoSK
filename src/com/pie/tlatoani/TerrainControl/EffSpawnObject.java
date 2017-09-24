package com.pie.tlatoani.TerrainControl;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.customobjects.CustomObject;
import com.khorn.terraincontrol.customobjects.CustomObjectCollection;
import com.khorn.terraincontrol.util.Rotation;
import org.bukkit.Location;
import org.bukkit.event.Event;

import java.util.Random;

public class EffSpawnObject extends Effect{
	private Expression<String> object;
	private Expression<Location> loc;
	private Expression<String> rotation;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		object = (Expression<String>) expr[0];
		loc = (Expression<Location>) expr[1];
		rotation = (Expression<String>) expr[2];
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return "tc spawn " + object + " at " + loc + " with rotation " + rotation;
	}

	@Override
	protected void execute(Event event) {
		int x = loc.getSingle(event).getBlockX();
		int y = loc.getSingle(event).getBlockY();
		int z = loc.getSingle(event).getBlockZ();
		Rotation r = null;
		if (rotation.getSingle(event).equalsIgnoreCase("NORTH")) {
			r = Rotation.NORTH;
		}
		if (rotation.getSingle(event).equalsIgnoreCase("EAST")) {
			r = Rotation.EAST;
		}
		if (rotation.getSingle(event).equalsIgnoreCase("SOUTH")) {
			r = Rotation.SOUTH;
		}
		if (rotation.getSingle(event).equalsIgnoreCase("WEST")) {
			r = Rotation.WEST;
		}
		String s = loc.getSingle(event).getWorld().getName();
		LocalWorld w = TerrainControl.getWorld(s);
		Random u = new Random(loc.getSingle(event).getWorld().getSeed());
		CustomObjectCollection c = new CustomObjectCollection();
		CustomObject o = c.getObjectByName(object.getSingle(event));
		o.spawnForced(w, u, r, x, y, z);
		
		
	}

}