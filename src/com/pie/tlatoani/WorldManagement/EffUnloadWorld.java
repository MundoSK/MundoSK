package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.Optional;

public class EffUnloadWorld extends Effect{
	private Expression<World> worldExpression;
	private Expression<Boolean> save;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		worldExpression = (Expression<World>) expr[0];
		save = (Expression<Boolean>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "unload " + worldExpression + (save == null ? "" : " save " + save);
	}

	@Override
	protected void execute(Event event) {
	    World world = worldExpression.getSingle(event);
		boolean save = Optional.ofNullable(this.save).map(expr -> expr.getSingle(event)).orElse(true);
		boolean successful = Bukkit.getServer().unloadWorld(world, save);
        if (!successful) {
            Logging.info("Failed to unload world " + world.getName());
        }
	}

}