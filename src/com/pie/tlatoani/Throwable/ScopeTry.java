package com.pie.tlatoani.Throwable;

import org.bukkit.event.Event;

import com.pie.tlatoani.Util.CustomScope;

import ch.njol.skript.lang.TriggerItem;

public class ScopeTry extends CustomScope {

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
		ExprCatch.catches.put(e, caught);
	}

}
