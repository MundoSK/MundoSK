package com.pie.tlatoani.WorldCreator;

import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprTypeOfCreator extends SimpleExpression<String>{
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
		WorldType type = creator.getSingle(arg0).type();
		if (type == WorldType.NORMAL) {
			result = "normal";
		}
		if (type == WorldType.AMPLIFIED) {
			result = "amplified";
		}
		if (type == WorldType.FLAT) {
			result = "flat";
		}
		if (type == WorldType.LARGE_BIOMES) {
			result = "large biomes";
		}
		if (type == WorldType.VERSION_1_1) {
			result = "version 1.1";
		}
		if (type == WorldType.CUSTOMIZED) {
			result = "customized";
		}
		return new String[]{result};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			if (((String)delta[0]).equalsIgnoreCase("normal")) {
				creator.getSingle(arg0).type(WorldType.NORMAL);
			}
			if (((String)delta[0]).equalsIgnoreCase("flat")) {
				creator.getSingle(arg0).type(WorldType.FLAT);
			}
			if (((String)delta[0]).equalsIgnoreCase("large biomes")) {
				creator.getSingle(arg0).type(WorldType.LARGE_BIOMES);
			}
			if (((String)delta[0]).equalsIgnoreCase("amplified")) {
				creator.getSingle(arg0).type(WorldType.AMPLIFIED);
			}
			if (((String)delta[0]).equalsIgnoreCase("version 1.1")) {
				creator.getSingle(arg0).type(WorldType.VERSION_1_1);
			}
			if (((String)delta[0]).equalsIgnoreCase("customized")) {
				creator.getSingle(arg0).type(WorldType.CUSTOMIZED);
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