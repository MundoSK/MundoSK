package com.pie.tlatoani.Throwable;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;

public class EffTry extends Effect{
	private static Method walkmethod;
	private TriggerItem section = null;
	
	static {
		try {
			walkmethod = TriggerItem.class.getDeclaredMethod("walk", Event.class);
			walkmethod.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "try";
	}

	@Override
	protected void execute(Event arg0) {
		Boolean within = true;
		TriggerItem going = section;
		Exception caught = null;
		while (within) {
			try {
				going = (TriggerItem) walkmethod.invoke(going, arg0);
				if (going == null || going.getIndentation().length() <= getIndentation().length()) within = false;
			} catch (Exception e) {
				within = false;
				caught = e;
			}
		}
		ExprCatch.catches.put(arg0, caught);
	}
	
	@Override
	protected TriggerItem walk(final Event e) {
		run(e);
		debug(e, true);
		return section.getNext();
	}
	
	@Override
	public TriggerItem setNext(final @Nullable TriggerItem next) {
		section = next;
		return this;
	}
	
	@Override
	public TriggerItem getNext() {
		return section.getNext();
	}

}