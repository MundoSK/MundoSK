package com.pie.tlatoani.Throwable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import org.bukkit.event.Event;

import com.pie.tlatoani.Util.CustomScope;

import ch.njol.skript.lang.TriggerItem;

public class ScopeTry extends CustomScope {
	private CondCatch condCatch = null;

	@Override
	public String toString(Event e, boolean debug) {
		return "try";
	}

	@Override
	public void go(Event e) {
		Boolean within = true;
		TriggerItem going = first;
		Exception caught = null;
		while (within) {
			try {
				going = (TriggerItem) walkmethod.invoke(going, e);
				if (going == null || going.getIndentation().length() <= indent) within = false;
			} catch (Exception e1) {
				within = false;
				caught = e1;
			}
		}
		if (condCatch != null) {
			condCatch.putCatch(e, ((caught != null) ? caught.getCause() : null));
		}
		ExprCatch.catches.put(e, ((caught != null) ? caught.getCause() : null));
	}

	@Override
	public void afterSetNext() {
		TriggerItem possibleCatch = section.getNext();
		if (possibleCatch instanceof Conditional) {
			try {
				Condition catchCond = (Condition) CustomScope.condition.get(possibleCatch);
				if (catchCond instanceof CondCatch) {
					this.condCatch = (CondCatch) catchCond;
				} else {
					Skript.warning("It is recommended to use a catch statement after a try statement");
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			Skript.warning("It is recommended to use a catch statement after a try statement");
		}
	}

}
