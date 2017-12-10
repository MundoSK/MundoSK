package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Util.CustomScope;
import com.pie.tlatoani.Util.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/26/16.
 */
public class ScopeWhen extends CustomScope {
    private Expression<Boolean> condition;
    private int delayTicks;

    @Override
    public String getString() {
        return "when " + condition;
    }

    @Override
    public boolean init() {
        condition = (Expression<Boolean>) exprs[0];
        delayTicks = exprs[1] == null ? 1 : new Long(((Literal<Timespan>) exprs[1]).getSingle().getTicks_i()).intValue();
        return true;
    }

    @Override
    public void setScope() {
        last.setNext(null);
    }

    @Override
    public boolean go(Event event) {
        if (condition.getSingle(event)) {
            TriggerItem.walk(first, event);
        } else {
            Scheduling.syncDelay(delayTicks, () -> go(event));
        }
        return false;
    }
}
