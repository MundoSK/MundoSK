package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.*;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class EffWait extends Effect {
    private Expression<Boolean> condition;
    private Expression<Timespan> timeoutExpr;
    private Expression<Timespan> delayExpr;
    private boolean until;
    private boolean sync;

    @Override
    protected TriggerItem walk(Event event) {
        long timeout = timeoutExpr == null ? -1 : timeoutExpr.getSingle(event).getTicks_i();
        int delay = delayExpr == null ? 1 : new Long(delayExpr.getSingle(event).getTicks_i()).intValue();
        check(event, timeout, delay, sync);
        return null;
    }

    @Override
    protected void execute(Event event) {}

    private void check(Event event, long timeout, int delay, boolean sync) {
        if (timeout == 0 || condition.getSingle(event) == until) {
            walk(getNext(), event);
        } else {
            if (sync) {
                Scheduling.syncDelay(delay, () -> check(event, timeout - 1, delay, true));
            } else {
                Scheduling.asyncDelay(delay, () -> check(event, timeout - 1, delay, false));
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return (sync ? "" : "async ") + "wait " + (until ? "until" : "while") + " " + condition + (timeoutExpr == null ? "" : " for " + timeoutExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        condition = (Expression<Boolean>) expressions[0];
        timeoutExpr = (Expression<Timespan>) expressions[1];
        delayExpr = (Expression<Timespan>) expressions[2];
        until = (parseResult.mark & 0b01) == 0;
        sync = (parseResult.mark & 0b10 ) == 0;
        return true;
    }
}
