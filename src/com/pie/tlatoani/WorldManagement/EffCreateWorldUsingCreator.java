package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.MundoUtil;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import org.bukkit.event.Event;

import java.util.Optional;

public class EffCreateWorldUsingCreator extends Effect{
	private Expression<String> worldName;
	private Expression<WorldCreatorData> creator;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
	    worldName = (Expression<String>) expressions[0];
		creator = (Expression<WorldCreatorData>) expressions[1];
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return "create new world" + (worldName != null ? " named " + worldName : "") + " using " + creator;
	}

	@Override
	protected void execute(Event event) {
        Optional<String> worldName = Optional.ofNullable(this.worldName).map(expr -> expr.getSingle(event));
        WorldCreatorData creator = this.creator.getSingle(event);
        MundoUtil.consumeOptional(worldName, creator::createWorld, () -> creator.name.ifPresent(__ -> creator.createWorld()));
	}

}