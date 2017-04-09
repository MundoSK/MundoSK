package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class EffWait extends Effect {
    private Expression<Boolean> condition;
    private Expression<Timespan> timeoutExpr;
    private boolean until;
    private boolean sync;

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
                Mundo.syncDelay(1, () -> check(event, timeout - 1, true));
            } else {
                Mundo.asyncDelay(1, () -> check(event, timeout - 1, false));
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
        return true;
    }
}
