package com.pie.tlatoani.Achievement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Achievement;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class EffAwardAch extends Effect{
	private Expression<Achievement> ach;
	private Expression<Player> player;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		ach = (Expression<Achievement>) expr[0];
		player = (Expression<Player>) expr[1];
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return " setSafely world border of world";
	}

	@Override
	protected void execute(Event event) {
		player.getSingle(event).awardAchievement(ach.getSingle(event));
		
		
	}

}