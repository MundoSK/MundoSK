package com.pie.tlatoani.Throwable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import com.pie.tlatoani.Util.CustomScope;

import ch.njol.skript.lang.TriggerItem;

public class ScopeTry extends CustomScope {
	private ScopeCatch scopeCatch = null;

	@Override
	public String getString() {
		return "try";
	}

	@Override
	public boolean go(Event event) {
		Exception caught = null;
		scope.setNext(null);
		try {
			TriggerItem go = first;
			while (go != null) {
			    go = (TriggerItem) CustomScope.TRIGGER_ITEM_WALK.invoke(go, event);
            }
		} catch (Exception e1) {
			caught = e1;
			Mundo.debug(this, "Exception caught");
			Mundo.debug(this, e1);
		}
		if (scopeCatch != null && caught != null) {;
			scopeCatch.catchThrowable(event, caught.getCause().getCause());
		}
		scope.setNext(scope.getNext());
		return false;
	}

	@Override
	public void setScope() {
		TriggerItem possibleCatch = scope.getNext();
		if (possibleCatch instanceof Conditional) {
			try {
				Condition catchCond = (Condition) CustomScope.condition.get(possibleCatch);
				if (catchCond instanceof ScopeCatch) {
					this.scopeCatch = (ScopeCatch) catchCond;
				} else {
					Skript.warning("It is recommended to use a catch statement after a try statement");
				}
			} catch (IllegalAccessException e) {
				Mundo.reportException(this, e);
			}
		} else {
			Skript.warning("It is recommended to use a catch statement after a try statement");
		}
		last.setNext(null);
	}

}
