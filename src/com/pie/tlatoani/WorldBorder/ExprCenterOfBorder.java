package com.pie.tlatoani.WorldBorder;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprCenterOfBorder extends SimpleExpression<Location>{
	private Expression<World> border;

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		border = (Expression<World>) expr[0];
		return true;
	}

	@Override
	public String toString(Event arg0, boolean arg1) {
		return "center of " + border;
	}

	@Override
	protected Location[] get(Event arg0) {
		WorldBorder border = this.border.getSingle(arg0).getWorldBorder();
		return new Location[]{ border.getCenter()};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET) {
			border.getSingle(arg0).getWorldBorder().setCenter((Location) delta[0]);
		}
	}

	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Location.class);
		}
		return null;
	}

}