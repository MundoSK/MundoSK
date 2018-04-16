package com.pie.tlatoani.Throwable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Util.Skript.CustomScope;
import com.pie.tlatoani.Util.Static.Logging;
import org.bukkit.event.Event;

public class ScopeTry extends CustomScope {
	private ScopeCatch scopeCatch = null;

	@Override
	public String getString() {
		return "try";
	}

	@Override
	public boolean go(Event event) {
		Exception caught = null;
		try {
			TriggerItem going = first;
            TriggerItem next = scope.getNext();
            Logging.debug(this, "First: " + first);
            Logging.debug(this, "Next: " + next);
			while (going != null && going != next) {
			    going = (TriggerItem) TRIGGER_ITEM_WALK.invoke(going, event);
                Logging.debug(this, "going: " + going);
            }
		} catch (Exception e) {
			caught = e;
			Logging.debug(this, "Exception caught");
			Logging.debug(this, e);
		}
		if (scopeCatch != null && caught != null) {;
			scopeCatch.catchThrowable(event, caught.getCause().getCause());
		}
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
				Logging.reportException(this, e);
			}
		} else {
			Skript.warning("It is recommended to use a catch statement after a try statement");
		}
		last.setNext(null);
	}

}
