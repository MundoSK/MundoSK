package com.pie.tlatoani.WorldBorder;

import org.bukkit.Location;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprBeyondBorder extends SimpleExpression<Boolean>{
	private Expression<Location> loc;
	private Boolean mp = true;

	@Override
	public Class<? extends Boolean> getReturnType() {
		// TODO Auto-generated method stub
		return Boolean.class;
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
		loc = (Expression<Location>) expr[0];
		if (arg3.mark == 0) mp = false;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	protected Boolean[] get(Event arg0) {
		Double posx = loc.getSingle(arg0).getWorld().getWorldBorder().getCenter().getX() + (loc.getSingle(arg0).getWorld().getWorldBorder().getSize() / 2);
		Double posz = loc.getSingle(arg0).getWorld().getWorldBorder().getCenter().getZ() + (loc.getSingle(arg0).getWorld().getWorldBorder().getSize() / 2);
		Double negx = loc.getSingle(arg0).getWorld().getWorldBorder().getCenter().getX() - (loc.getSingle(arg0).getWorld().getWorldBorder().getSize() / 2);
		Double negz = loc.getSingle(arg0).getWorld().getWorldBorder().getCenter().getZ() - (loc.getSingle(arg0).getWorld().getWorldBorder().getSize() / 2);
		Boolean result = false;
		if (loc.getSingle(arg0).getX() > posx || loc.getSingle(arg0).getX() < negx) result = true;
		if (loc.getSingle(arg0).getZ() > posz || loc.getSingle(arg0).getZ() < negz) result = true;
		return new Boolean[]{Boolean.logicalXor(result, mp)};
	}


}