package com.pie.tlatoani.Util;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffCallCustomEvent extends Effect{
	private Expression<String> id;
	private Expression<Number> num;
	private Expression<String> str;
	private Expression<Boolean> boo;
	private Expression<Object> args;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		id = (Expression<String>) expr[0];
		num = (Expression<Number>) expr[1];
		str = (Expression<String>) expr[2];
		boo = (Expression<Boolean>) expr[3];
		args = (Expression<Object>) expr[4];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "call custom event";
	}

	@Override
	protected void execute(Event arg0) {
		UtilCustomEvent event = new UtilCustomEvent(id.getSingle(arg0), (num != null ? num.getSingle(arg0) : null), (str!= null ? str.getSingle(arg0) : null), (boo != null ? boo.getSingle(arg0) : null), (args != null ? args.getAll(arg0) : null));
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

}