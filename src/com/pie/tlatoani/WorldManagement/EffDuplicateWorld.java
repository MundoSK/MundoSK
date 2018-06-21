package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.io.File;
import java.io.IOException;

public class EffDuplicateWorld extends Effect{
	private Expression<World> world;
	private Expression<String> name;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		world = (Expression<World>) expr[0];
		name = (Expression<String>) expr[1];
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return "duplicate " + world + " using name " + name;
	}

	@Override
	protected void execute(Event event) {
        World oldWorld = this.world.getSingle(event);
		String name = this.name.getSingle(event);
		File newWorldFolder = new File(name);
		newWorldFolder.mkdir();
		File oldWorldFolder = oldWorld.getWorldFolder();
		try {
			FileUtils.copyDirectory(oldWorldFolder, newWorldFolder);
		} catch (IOException e) {
			Logging.reportException(this, e);
		}
		File uidDatFile = new File(name + "/uid.dat");
		uidDatFile.delete();
        WorldCreatorData.fromWorld(oldWorld).createWorld(name);
		
		
	}
	

}