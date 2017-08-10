package com.pie.tlatoani.Socket;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.Logging;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffOpenFunctionSocket extends Effect{
	private Expression<Number> port;
	private Expression<String> pass;
	private Expression<String> handler;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		port = (Expression<Number>) expr[0];
		pass = (Expression<String>) expr[1];
		handler = (Expression<String>) expr[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " setSafely world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		String passarg = null;
		if (pass != null) passarg = pass.getSingle(arg0);
		String handlerarg = null;
		if (handler != null) handlerarg = handler.getSingle(arg0);
		Logging.debug(this, "Passarg : " + passarg + ", Handlerarg : " + handlerarg);
		Logging.debug(this, "port : " + port + "port.getSingle : " + port.getSingle(arg0));
		UtilFunctionSocket.openFunctionSocket(port.getSingle(arg0).intValue(), passarg, handlerarg);
		
	}
	

}