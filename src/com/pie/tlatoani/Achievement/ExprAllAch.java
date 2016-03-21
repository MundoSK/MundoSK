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

public class ExprAllAch extends SimpleExpression<String>{
	private static String[] achieves = new String[Achievement.values().length];
	static {
		for (int a = 0; a < Achievement.values().length; a++) {
			achieves[a] = Achievement.values()[a].toString();
		}
	}
	private Expression<Player> player;

	@Override
	public Class<? extends String> getReturnType() {
		// TODO Auto-generated method stub
		return String.class;
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
	protected String[] get(Event arg0) {
		if (player == null) return achieves;
		else {
			List<String> list = new LinkedList<String>();
			for (int a = 0; a < Achievement.values().length; a++) {
				if (player.getSingle(arg0).hasAchievement(Achievement.values()[a])) list.add(Achievement.values()[a].toString());
			}
			return (String[]) list.toArray();
		}
	}
	
	public Iterator<String> iterator(Event arg0) {
		if (player == null) return Arrays.asList(achieves).iterator();
		else {
			List<String> list = new LinkedList<String>();
			for (int a = 0; a < Achievement.values().length; a++) {
				if (player.getSingle(arg0).hasAchievement(Achievement.values()[a])) list.add(Achievement.values()[a].toString());
			}
			return list.iterator();
		}
	}

}