package com.pie.tlatoani.Achievement;

import java.util.*;

import javax.annotation.Nullable;

import org.bukkit.Achievement;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprAllAch extends SimpleExpression<Achievement>{
	private Expression<Player> player;

	@Override
	public Class<? extends Achievement> getReturnType() {
		return Achievement.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		player = (Expression<Player>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return player + "'s achievements";
	}

	@Override
	@Nullable
	protected Achievement[] get(Event arg0) {
		List<Achievement> list = new ArrayList<>();
		for (int a = 0; a < Achievement.values().length; a++) {
			if (player.getSingle(arg0).hasAchievement(Achievement.values()[a])) list.add(Achievement.values()[a]);
		}
		return list.toArray(new Achievement[list.size()]);
	}

	public Iterator<Achievement> iterator(Event arg0) {
		List<Achievement> list = new ArrayList<>();
		for (int a = 0; a < Achievement.values().length; a++) {
			if (player.getSingle(arg0).hasAchievement(Achievement.values()[a])) list.add(Achievement.values()[a]);
		}
		return list.iterator();
	}

}