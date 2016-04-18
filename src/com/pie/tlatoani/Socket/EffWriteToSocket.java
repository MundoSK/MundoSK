package com.pie.tlatoani.Socket;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

public class EffWriteToSocket extends Effect{
	private Expression<String> msgs;
	private Expression<String> ip;
	private Expression<Number> port;
	private Expression<Timespan> timeout;
	private Expression<String> redirect;
	private Expression<String> report;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		msgs = (Expression<String>) expr[0];
		ip = (Expression<String>) expr[2];
		port = (Expression<Number>) expr[3];
		timeout = (Expression<Timespan>) expr[4];
		redirect = (Expression<String>) expr[5];
		report = (Expression<String>) expr[6];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return " set world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		String redirectarg = null;
		String reportarg = null;
		Integer timeoutarg = null;
		if (redirect != null) {
			redirectarg = redirect.getSingle(arg0);
			reportarg = report.getSingle(arg0);
		}
		if (timeout != null && timeout.getSingle(arg0).getMilliSeconds() < Integer.MAX_VALUE) timeoutarg = (int) timeout.getSingle(arg0).getMilliSeconds();
		UtilWriterSocket exec = new UtilWriterSocket(msgs.getAll(arg0), ip.getSingle(arg0), port.getSingle(arg0).intValue(), redirectarg, reportarg, timeoutarg);
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("MundoSK"), exec);
	}
	

}