package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.io.IOException;

public class EffDeleteWorld extends Effect{
	private Expression<World> worldExpression;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		worldExpression = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return "delete " + worldExpression;
	}

	@Override
	protected void execute(Event event) {
	    World world = worldExpression.getSingle(event);
		boolean successful = Bukkit.getServer().unloadWorld(world, true);
		if (!successful) {
		    Logging.info("Failed to delete world " + world.getName());
		    return;
        }
		try {
			FileUtils.deleteDirectory(world.getWorldFolder());
		} catch (IOException e) {
			Logging.reportException(this, e);
		}
		
		
	}
	

}