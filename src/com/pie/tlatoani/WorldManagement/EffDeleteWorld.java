package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.Logging;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.io.File;
import java.io.IOException;

public class EffDeleteWorld extends Effect{
	private Expression<World> world;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		world = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return "delete " + world;
	}

	@Override
	protected void execute(Event event) {
		File f = world.getSingle(event).getWorldFolder();
		Bukkit.getServer().unloadWorld(world.getSingle(event), true);
		try {
			FileUtils.deleteDirectory(f);
		} catch (IOException e) {
			Logging.reportException(this, e);
		}
		
		
	}
	

}