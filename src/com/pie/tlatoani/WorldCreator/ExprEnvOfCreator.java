package com.pie.tlatoani.WorldCreator;

import org.bukkit.World.Environment;
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

public class ExprEnvOfCreator extends SimpleExpression<Environment>{
	private Expression<WorldCreator> creator;

	@Override
	public Class<? extends Environment> getReturnType() {
		// TODO Auto-generated method stub
		return Environment.class;
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
	protected Environment[] get(Event arg0) {
		return new Environment[]{creator.getSingle(arg0).environment()};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			creator.getSingle(arg0).environment((Environment)delta[0]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Environment.class);
		}
		return null;
	}

}