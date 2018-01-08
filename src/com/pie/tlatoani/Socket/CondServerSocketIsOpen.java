package com.pie.tlatoani.Socket;

import java.net.InetSocketAddress;
import java.net.Socket;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

public class CondServerSocketIsOpen extends SimpleExpression<Boolean>{
	private Expression<String> host;
	private Expression<Number> port;
	private Expression<Timespan> timeout;

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
		host = (Expression<String>) expr[0];
		port = (Expression<Number>) expr[1];
		timeout = (Expression<Timespan>) expr[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	protected Boolean[] get(Event event) {
		Boolean result = false;
		try {
			Socket attempt = new Socket();
			Integer timeoutarg = 0;
			if (timeout != null && timeout.getSingle(event).getMilliSeconds() <= Integer.MAX_VALUE) timeoutarg = (int) timeout.getSingle(event).getMilliSeconds();
			attempt.connect(new InetSocketAddress(host.getSingle(event), port.getSingle(event).intValue()), timeoutarg);
			attempt.close();
			result = true;
		} catch (Exception e) {}
		return new Boolean[]{result};
	}


}