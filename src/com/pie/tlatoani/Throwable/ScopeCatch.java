package com.pie.tlatoani.Throwable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Util.Skript.CustomScope;
import com.pie.tlatoani.Util.Static.Logging;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 8/16/16.
 */
public class ScopeCatch extends CustomScope {
    private Expression<?> container;

    @Override
    public String getString() {
        return "catch in " + container;
    }

    @Override
    public boolean init() {
        container = exprs[0];
        Class[] classes = container.acceptChange(Changer.ChangeMode.SET);
        if (!(Arrays.asList(classes).contains(Throwable.class) || Arrays.asList(classes).contains(Object.class))) {
            Skript.error("The expression " + container + " cannot be setSafely to an exception! The expression of a catch statement needs to be able to catch an exception.");
            return false;
        }
        return true;
    }

    public void catchThrowable(Event event, Throwable caught) {
        container.change(event, new Throwable[]{caught}, Changer.ChangeMode.SET);
        if (scope == null) {
            if (function != null) {
                scopeParent = SCRIPT_FUNCTION_TRIGGER.get(function);
            }
            if (scopeParent != null) {
                retrieveScope();
            } else {
                getScopes();
            }
        }
        TriggerItem going = first;
        TriggerItem next = scope.getNext();
        Logging.debug(this, "First: " + first);
        Logging.debug(this, "Next: " + next);
        while (going != null && going != next) {
            going = (TriggerItem) TRIGGER_ITEM_WALK.invoke(going, event);
            Logging.debug(this, "going: " + going);
        }
    }
}
