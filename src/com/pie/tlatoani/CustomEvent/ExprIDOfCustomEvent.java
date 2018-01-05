package com.pie.tlatoani.CustomEvent;

import javax.annotation.Nullable;

import ch.njol.util.coll.iterator.EmptyIterator;
import org.bukkit.event.Event;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import java.util.Iterator;

public class ExprIDOfCustomEvent extends SimpleExpression<String>{
	private boolean single;

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return single;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(SkriptCustomEvent.class)) {
			Skript.error("Cannot use 'custom event's id' outside of custom events");
			return false;
		}
		single = arg3.mark == 0;
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return "custom event's id" + (single ? "" : "s");
	}

	@Override
	@Nullable
	protected String[] get(Event event) {
	    if (event instanceof SkriptCustomEvent) {
	        SkriptCustomEvent customEvent = (SkriptCustomEvent) event;
	        return single ? new String[]{customEvent.getPrimaryID()} : customEvent.ids.toArray(new String[0]);
        }
		return new String[0];
	}

	@Override
    public Iterator<String> iterator(Event event) {
	    if (single) {
	        throw new IllegalStateException("This expression is single, cannot be iterated!");
        }
	    if (event instanceof SkriptCustomEvent) {
	        return ((SkriptCustomEvent) event).ids.iterator();
        }
        return new EmptyIterator<>();
    }

}