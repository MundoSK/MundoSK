package com.pie.tlatoani.Miscellaneous.Thread;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Util.CustomScope;
import com.pie.tlatoani.Util.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class ScopeAsync extends CustomScope {
    private Expression<Timespan> delay;

    public ScopeAsync() {
        canStandFree = true;
    }

    @Override
    public String getString() {
        return "async" + (delay == null ? "" : " in " + delay);
    }

    @Override
    public void setScope() {
        last.setNext(null);
    }

    @Override
    public boolean init() {
        delay = (Expression<Timespan>) exprs[0];
        return true;
    }

    @Override
    public TriggerItem walk(Event event) {
        go(event);
        return null;
    }

    @Override
    public boolean go(Event event) {
        Runnable runnable = () -> TriggerItem.walk(scope == null ? getNext() : first, event);
        if (delay == null) {
            Scheduling.async(runnable);
        } else {
            Scheduling.asyncDelay(new Long(delay.getSingle(event).getTicks_i()).intValue(), runnable);
        }
        return false;
    }
}
