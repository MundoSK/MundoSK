package com.pie.tlatoani.Socket;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

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
		ip = (Expression<String>) expr[1];
		port = (Expression<Number>) expr[2];
		timeout = (Expression<Timespan>) expr[3];
		redirect = (Expression<String>) expr[4];
		report = (Expression<String>) expr[5];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return " setSafely world border of world";
	}

	@Override
	protected void execute(Event event) {
		String redirectarg = null;
		String reportarg = null;
		Integer timeoutarg = null;
		if (redirect != null) {
			redirectarg = redirect.getSingle(event);
			reportarg = report.getSingle(event);
		}
		if (timeout != null && timeout.getSingle(event).getMilliSeconds() < Integer.MAX_VALUE) timeoutarg = (int) timeout.getSingle(event).getMilliSeconds();
		UtilWriterSocket exec = new UtilWriterSocket(msgs.getArray(event), ip.getSingle(event), port.getSingle(event).intValue(), redirectarg, reportarg, timeoutarg);
        Scheduling.async(exec);
    }
	

}