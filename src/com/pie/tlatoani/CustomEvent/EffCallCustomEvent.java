package com.pie.tlatoani.CustomEvent;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffCallCustomEvent extends Effect{
	private Expression<Object> details;
	private Expression<String> id;
	private Expression<Object> args;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		details = (Expression<Object>) expr[1];
		id = (Expression<String>) expr[0];
		args = (Expression<Object>) expr[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "call custom event";
	}

	@Override
	protected void execute(Event arg0) {
		String id = this.id.getSingle(arg0);
		Object[] details = this.details != null ? this.details.getArray(arg0) : null;
		Object[] args = this.args != null ? this.args.getArray(arg0) : null;
		UtilCustomEvent event = new UtilCustomEvent(id, details, args);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

}