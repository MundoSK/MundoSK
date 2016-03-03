package com.pie.tlatoani.Socket;

import java.io.IOException;
import java.net.Socket;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondServerSocketIsOpen extends Condition {
	private Expression<String> host;
	private Expression<Integer> port;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		host = (Expression<String>) expr[0];
		port = (Expression<Integer>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	public boolean check(Event arg0) {
		Boolean result = true;
		try {
			Socket attempt = new Socket(host.getSingle(arg0), port.getSingle(arg0));
			attempt.close();
		} catch (IOException e) {
			result = false;
		}
		return result;
	}


}