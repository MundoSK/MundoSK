package com.pie.tlatoani.Socket;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffWriteToSocket extends Effect{
	private Expression<String> msgs;
	private Expression<String> ip;
	private Expression<Integer> port;
	private Expression<String> redirect;
	private Expression<String> report;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern,
			Kleenean paramKleenean, ParseResult paramParseResult) {
		msgs = (Expression<String>) expr[0];
		ip = (Expression<String>) expr[1];
		port = (Expression<Integer>) expr[2];
		redirect = (Expression<String>) expr[3];
		report = (Expression<String>) expr[4];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " set world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		UtilWriterSocket exec;
		if (redirect == null) {
			exec = new UtilWriterSocket(msgs.getAll(arg0), ip.getSingle(arg0), port.getSingle(arg0));
		} else {
			exec = new UtilWriterSocket(msgs.getAll(arg0), ip.getSingle(arg0), port.getSingle(arg0), redirect.getSingle(arg0), report.getSingle(arg0));
		}
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("MundoSK"), exec);
	}
	

}