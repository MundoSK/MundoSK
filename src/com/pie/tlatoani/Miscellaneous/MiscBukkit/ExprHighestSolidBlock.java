package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.World;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprHighestSolidBlock extends SimpleExpression<Block>{
	private Expression<Location> loc;

	@Override
	public Class<? extends Block> getReturnType() {
		// TODO Auto-generated method stub
		return Block.class;
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
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected Block[] get(Event event) {
		World w = loc.getSingle(event).getWorld();
		Block b = w.getHighestBlockAt(loc.getSingle(event));
		return new Block[]{b};
	}

}