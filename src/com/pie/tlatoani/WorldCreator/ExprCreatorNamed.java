package com.pie.tlatoani.WorldCreator;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import javax.annotation.Nullable;

import org.bukkit.WorldType;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprCreatorNamed extends SimpleExpression<WorldCreator>{
	private Expression<String> name;

	@Override
	public Class<? extends WorldCreator> getReturnType() {
		// TODO Auto-generated method stub
		return WorldCreator.class;
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
		name = (Expression<String>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected WorldCreator[] get(Event arg0) {
		String b = name.getSingle(arg0);
		WorldCreator x = new WorldCreator(b);
		x.generateStructures(true);
		x.environment(World.Environment.NORMAL);
		x.type(WorldType.NORMAL);
		return new WorldCreator[]{x};
	}

}