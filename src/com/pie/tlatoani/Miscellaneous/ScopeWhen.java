package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Core.Skript.CustomScope;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/26/16.
 */
public class ScopeWhen extends CustomScope {
    private Expression<Boolean> condition;
    private Expression<Timespan> delayExpr;

    @Override
    public String getString() {
        return "when " + condition;
    }

    @Override
    public boolean init() {
        condition = (Expression<Boolean>) exprs[0];
        delayExpr = (Expression<Timespan>) exprs[1];
        return true;
    }

    @Override
    public void setScope() {
        last.setNext(null);
    }

    @Override
    public boolean go(Event event) {
        int delayTicks = exprs[1] == null ? 1 : new Long(delayExpr.getSingle(event).getTicks_i()).intValue();
        go(event, delayTicks);
        return false;
    }

    private void go(Event event, int delayTicks) {
        if (condition.getSingle(event)) {
            TriggerItem.walk(first, event);
        } else {
            Scheduling.syncDelay(delayTicks, () -> go(event, delayTicks));
        }
    }
}
