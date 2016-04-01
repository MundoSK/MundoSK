package com.pie.tlatoani.Throwable;

import java.lang.reflect.Method;

import org.bukkit.event.Event;

import com.pie.tlatoani.Util.CustomScope;

import ch.njol.skript.lang.TriggerItem;

public class ScopeTry extends CustomScope {
	private static Method walkmethod;
	
	static {
		try {
			walkmethod = TriggerItem.class.getDeclaredMethod("walk", Event.class);
			walkmethod.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "try";
	}

	@Override
	public void go(Event e, TriggerItem next, Integer indent) {
		Boolean within = true;
		TriggerItem going = next;
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
