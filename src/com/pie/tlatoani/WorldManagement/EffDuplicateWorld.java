package com.pie.tlatoani.WorldManagement;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffDuplicateWorld extends Effect{
	private Expression<World> world;
	private Expression<String> name;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		// TODO Auto-generated method stub
		world = (Expression<World>) expr[0];
		name = (Expression<String>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " setSafely world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		new File(name.getSingle(arg0)).mkdir();
		File t = world.getSingle(arg0).getWorldFolder();
		File d = new File(name.getSingle(arg0));
		try {
			org.apache.commons.io.FileUtils.copyDirectory(t, d);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WorldCreator c = new WorldCreator(name.getSingle(arg0));
		File x = new File(name.getSingle(arg0) + "/uid.dat");
		x.delete();
		c.copy(world.getSingle(arg0));
		c.createWorld();
		
		
	}
	
	public Integer[][] x(Integer[] a) {
		Integer[][] b = new Integer[a.length][];
		for (int i = 1;i <= a.length;i++) {
			
		}
		return b;
	}
	

}