package com.pie.tlatoani.CustomEvent;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class EffCallCustomEvent extends Effect{
	private Expression<Object> details;
	private Expression<String> ids;
	private Expression<Object> args;
	private boolean sync;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		details = (Expression<Object>) expr[1];
		ids = (Expression<String>) expr[0];
		args = (Expression<Object>) expr[2];
		sync = paramParseResult.mark == 0;
		return true;
	}

	@Override
	public String toString(Event paramEvent, boolean paramBoolean) {
		return (sync ? "" : "async ") + "call custom event " + ids + (details == null ? "" : " details " + details) + (args == null ? "" : " arguments " + args);
	}

	@Override
	protected void execute(Event event) {
		String[] ids = this.ids.getArray(event);
		if (ids.length == 0) {
			return;
		}
		for (int i = 0; i < ids.length; i++) {
		    ids[i] = ids[i].toLowerCase();
        }
		Object[] details = this.details != null ? this.details.getArray(event) : new Object[0];
		Object[] args = this.args != null ? this.args.getArray(event) : new Object[0];
		SkriptCustomEvent customEvent = new SkriptCustomEvent(ids, details, args, sync);
		Bukkit.getServer().getPluginManager().callEvent(customEvent);
		SkriptCustomEvent.lastCustomEvents.put(event, customEvent);
	}

}