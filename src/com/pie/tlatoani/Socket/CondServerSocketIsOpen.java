package com.pie.tlatoani.Socket;

import java.net.InetSocketAddress;
import java.net.Socket;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

public class CondServerSocketIsOpen extends Condition {
	private Expression<String> host;
	private Expression<Integer> port;
	private Expression<Timespan> timeout;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		host = (Expression<String>) expr[0];
		port = (Expression<Integer>) expr[1];
		timeout = (Expression<Timespan>) expr[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	public boolean check(Event arg0) {
		try {
			Socket attempt = new Socket();
			Integer timeoutarg = 0;
			if (timeout != null && timeout.getSingle(arg0).getMilliSeconds() <= Integer.MAX_VALUE) timeoutarg = (int) timeout.getSingle(arg0).getMilliSeconds();
			attempt.connect(new InetSocketAddress(host.getSingle(arg0), port.getSingle(arg0)), timeoutarg);
			attempt.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}


}