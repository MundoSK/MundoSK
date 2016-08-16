package com.pie.tlatoani.Throwable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
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
        if (caught != null) {
            container.change(event, new Throwable[]{caught}, Changer.ChangeMode.SET);
            TriggerItem going = first;
            TriggerItem end = scope.getNext();
            Mundo.debug(this, "First: " + first);
            Mundo.debug(this, "End: " + end);
            while (going != null && going != end) {
                try {
                    going = (TriggerItem) walkmethod.invoke(going, event);
                    Mundo.debug(this, "going: " + going);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    going = null;
                }
            }
        }
    }
}
