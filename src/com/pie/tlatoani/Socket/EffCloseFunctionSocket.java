package com.pie.tlatoani.Socket;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffCloseFunctionSocket extends Effect{
	private Expression<Number> port;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		port = (Expression<Number>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " setSafely world border of world";
	}

	@Override
	protected void execute(Event event) {
		UtilFunctionSocket.closeFunctionSocket(port.getSingle(event).intValue());
	}
	

}