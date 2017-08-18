package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.Optional;

public class EffUnloadWorld extends Effect{
	private Expression<World> world;
	private Expression<Boolean> save;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		world = (Expression<World>) expr[0];
		save = (Expression<Boolean>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "unload " + world + (save == null ? "" : " save " + save);
	}

	@Override
	protected void execute(Event event) {
		Boolean save = Optional.ofNullable(this.save).map(expr -> expr.getSingle(event)).orElse(true);
		Bukkit.getServer().unloadWorld(world.getSingle(event), save);
		
		
	}

}