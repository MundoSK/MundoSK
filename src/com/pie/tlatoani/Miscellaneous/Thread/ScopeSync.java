package com.pie.tlatoani.Miscellaneous.Thread;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Util.Skript.CustomScope;
import com.pie.tlatoani.Util.Static.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class ScopeSync extends CustomScope {
    private Expression<Timespan> delay;

    public ScopeSync() {
        canStandFree = true;
    }

    @Override
    public String getString() {
        return delay == null ? "sync" : "in " + delay;
    }

    @Override
    public void setScope()   {
        if (last != null) {
            last.setNext(null);
        }
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
            Scheduling.sync(runnable);
        } else {
            Scheduling.syncDelay(new Long(delay.getSingle(event).getTicks_i()).intValue(), runnable);
        }
        return false;
    }
}
