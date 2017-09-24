package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import org.bukkit.World;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprGameRule extends SimpleExpression<String>{
	private Expression<World> world;
	private Expression<String> rule;

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		rule = (Expression<String>) expr[0];
		world = (Expression<World>) expr[1];
		return true;
	}

	@Override
	public String toString(Event event, boolean arg1) {
		return "value of gamerule " + rule + " in " + world;
	}

	@Override
	protected String[] get(Event event) {
		return new String[]{world.getSingle(event).getGameRuleValue(rule.getSingle(event))};
	}
	
	public void change(Event event, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			world.getSingle(event).setGameRuleValue(rule.getSingle(event), (String)delta[0]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(String.class);
		}
		return null;
	}

}