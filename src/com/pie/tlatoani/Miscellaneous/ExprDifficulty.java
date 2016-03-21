package com.pie.tlatoani.Miscellaneous;

import org.bukkit.Difficulty;
import org.bukkit.World;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprDifficulty extends SimpleExpression<String>{
	private Expression<World> world;

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
		world = (Expression<World>) expr[0];
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
		if (world.getSingle(arg0).getDifficulty() == Difficulty.PEACEFUL) {
			result = "peaceful";
		}
		if (world.getSingle(arg0).getDifficulty() == Difficulty.EASY) {
			result = "easy";
		}
		if (world.getSingle(arg0).getDifficulty() == Difficulty.NORMAL) {
			result = "normal";
		}
		if (world.getSingle(arg0).getDifficulty() == Difficulty.HARD) {
			result = "hard";
		}
		return new String[]{result};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			if (((String) delta[0]).equalsIgnoreCase("PEACEFUL")) {
				world.getSingle(arg0).setDifficulty(Difficulty.PEACEFUL);
			}
			if (((String) delta[0]).equalsIgnoreCase("EASY")) {
				world.getSingle(arg0).setDifficulty(Difficulty.EASY);
			}
			if (((String) delta[0]).equalsIgnoreCase("NORMAL")) {
				world.getSingle(arg0).setDifficulty(Difficulty.NORMAL);
			}
			if (((String) delta[0]).equalsIgnoreCase("HARD")) {
				world.getSingle(arg0).setDifficulty(Difficulty.HARD);
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