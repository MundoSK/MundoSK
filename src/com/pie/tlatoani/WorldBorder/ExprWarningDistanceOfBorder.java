package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;
import org.bukkit.WorldBorder;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprWarningDistanceOfBorder extends SimpleExpression<Integer>{
	private Expression<World> border;

	@Override
	public Class<? extends Integer> getReturnType() {
		// TODO Auto-generated method stub
		return Integer.class;
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
		border = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected Integer[] get(Event arg0) {
		// TODO Auto-generated method stub
		WorldBorder b = border.getSingle(arg0).getWorldBorder();
		return new Integer[]{ b.getWarningDistance()};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			border.getSingle(arg0).getWorldBorder().setWarningDistance(((Long) delta[0]).intValue());
		}
		if (mode == ChangeMode.ADD) {
			border.getSingle(arg0).getWorldBorder().setWarningDistance(((Long) delta[0]).intValue() + border.getSingle(arg0).getWorldBorder().getWarningDistance());
		}
		if (mode == ChangeMode.REMOVE) {
			border.getSingle(arg0).getWorldBorder().setWarningDistance(border.getSingle(arg0).getWorldBorder().getWarningDistance() - ((Long) delta[0]).intValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Long.class);
		}
		if (mode == ChangeMode.ADD) {
			return CollectionUtils.array(Long.class);
		}
		if (mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(Long.class);
		}
		return null;
	}

}