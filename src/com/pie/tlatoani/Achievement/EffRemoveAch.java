package com.pie.tlatoani.Achievement;

import org.bukkit.Achievement;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffRemoveAch extends Effect{
	private Expression<String> ach;
	private Expression<Player> player;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		// TODO Auto-generated method stub
		ach = (Expression<String>) expr[0];
		player = (Expression<Player>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " set world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		player.getSingle(arg0).removeAchievement(Achievement.valueOf(ach.getSingle(arg0)));
		
		
	}

}