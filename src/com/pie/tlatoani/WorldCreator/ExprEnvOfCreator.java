package com.pie.tlatoani.WorldCreator;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprEnvOfCreator extends SimpleExpression<String>{
	private Expression<WorldCreator> creator;

	@Override
	public Class<? extends String> getReturnType() {
		// TODO Auto-generated method stub
		return String.class;
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
		creator = (Expression<WorldCreator>) expr[0];
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
		String result = null;
		if (creator.getSingle(arg0).environment() == World.Environment.NORMAL) {
			result = "normal";
		}
		if (creator.getSingle(arg0).environment() == World.Environment.NETHER) {
			result = "nether";
		}
		if (creator.getSingle(arg0).environment() == World.Environment.THE_END) {
			result = "end";
		}
		return new String[]{result};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			if (((String)delta[0]).equalsIgnoreCase("NORMAL")) {
				creator.getSingle(arg0).environment(World.Environment.NORMAL);
			}
			if (((String)delta[0]).equalsIgnoreCase("NETHER")) {
				creator.getSingle(arg0).environment(World.Environment.NETHER);
			}
			if (((String)delta[0]).equalsIgnoreCase("END")) {
				creator.getSingle(arg0).environment(World.Environment.THE_END);
			}
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