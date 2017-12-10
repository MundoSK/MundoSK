package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.*;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class EffWait extends Effect {
    private Expression<Boolean> condition;
    private Expression<Timespan> timeoutExpr;
    private boolean until;
    private boolean sync;
    private int delayTicks;

    @Override
    protected TriggerItem walk(Event event) {
        long timeout = timeoutExpr == null ? -1 : timeoutExpr.getSingle(event).getTicks_i();
        check(event, timeout, sync);
        return null;
    }

    @Override
    protected void execute(Event event) {}

    private void check(Event event, long timeout, boolean sync) {
        if (timeout == 0 || condition.getSingle(event) == until) {
            walk(getNext(), event);
        } else {
            if (sync) {
                Scheduling.syncDelay(1, () -> check(event, timeout - 1, true));
            } else {
                Scheduling.asyncDelay(1, () -> check(event, timeout - 1, false));
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "wait " + (until ? "until" : "while") + " " + condition + (timeoutExpr == null ? "" : " for " + timeoutExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        condition = (Expression<Boolean>) expressions[0];
        timeoutExpr = (Expression<Timespan>) expressions[1];
        until = (parseResult.mark & 0b01) == 0;
        sync = (parseResult.mark & 0b10 ) == 0;
        delayTicks = expressions[2] == null ? 1 : new Long(((Literal<Timespan>) expressions[2]).getSingle().getTicks_i()).intValue();
        return true;
    }
}
