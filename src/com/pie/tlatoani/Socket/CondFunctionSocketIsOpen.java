package com.pie.tlatoani.Socket;

import javax.annotation.Nullable;

import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class CondFunctionSocketIsOpen extends SimpleExpression<Boolean>{
	private Expression<Number> port;

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
		port = (Expression<Number>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	protected Boolean[] get(Event event) {
		Logging.debug(this, "port = " + port);
		Number number = port.getSingle(event);
		Logging.debug(this, "number = " + number);
		int i = number.intValue();
		Logging.debug(this, "i = " + i);
		return new Boolean[]{UtilFunctionSocket.getStatusOfFunctionSocket(i)};
	}


}