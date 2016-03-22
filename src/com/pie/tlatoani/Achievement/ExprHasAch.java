package com.pie.tlatoani.Achievement;

import org.bukkit.Achievement;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprHasAch extends SimpleExpression<Boolean>{
	private Expression<Player> player;
	private Expression<Achievement> ach;

	@Override
	public Class<? extends Boolean> getReturnType() {
		// TODO Auto-generated method stub
		return Boolean.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		// TODO Auto-generated method stub
		player = (Expression<Player>) expr[0];
		ach = (Expression<Achievement>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	protected Boolean[] get(Event arg0) {
		return new Boolean[]{player.getSingle(arg0).hasAchievement(ach.getSingle(arg0))};
	}


}