package com.pie.tlatoani.Achievement;

import org.bukkit.Achievement;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondHasAch extends Condition {
	private Expression<Player> player;
	private Expression<String> ach;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		// TODO Auto-generated method stub
		player = (Expression<Player>) expr[0];
		ach = (Expression<String>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	public boolean check(Event arg0) {
		return player.getSingle(arg0).hasAchievement(Achievement.valueOf(ach.getSingle(arg0)));
	}


}