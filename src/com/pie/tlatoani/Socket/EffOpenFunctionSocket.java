package com.pie.tlatoani.Socket;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffOpenFunctionSocket extends Effect{
	private Expression<Integer> port;
	private Expression<String> pass;
	private Expression<String> handler;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		port = (Expression<Integer>) expr[0];
		pass = (Expression<String>) expr[1];
		handler = (Expression<String>) expr[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " set world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		String passarg = null;
		if (pass != null) passarg = pass.getSingle(arg0);
		String handlerarg = null;
		if (handler != null) handlerarg = handler.getSingle(arg0);
		UtilFunctionSocket.openFunctionSocket(port.getSingle(arg0), passarg, handlerarg);
		
	}
	

}