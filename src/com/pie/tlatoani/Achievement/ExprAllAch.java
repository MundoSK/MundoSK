package com.pie.tlatoani.Achievement;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
		// TODO Auto-generated method stub
		return Achievement.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected Achievement[] get(Event arg0) {
		if (player == null) return Achievement.values();
		else {
			List<Achievement> list = new LinkedList<Achievement>();
			for (int a = 0; a < Achievement.values().length; a++) {
				if (player.getSingle(arg0).hasAchievement(Achievement.values()[a])) list.add(Achievement.values()[a]);
			}
			return list.toArray(new Achievement[list.size()]);
		}
	}
	
	public Iterator<Achievement> iterator(Event arg0) {
		if (player == null) return Arrays.asList(Achievement.values()).iterator();
		else {
			List<Achievement> list = new LinkedList<Achievement>();
			for (int a = 0; a < Achievement.values().length; a++) {
				if (player.getSingle(arg0).hasAchievement(Achievement.values()[a])) list.add(Achievement.values()[a]);
			}
			return list.iterator();
		}
	}

}