package com.pie.tlatoani.Miscellaneous.Thread;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 3/29/17.
 */
public class EffWaitAsync extends Effect {
    private Expression<Timespan> delay;

    @Override
    protected void execute(Event event) {}

    @Override
    public TriggerItem walk(Event event) {
        Scheduling.asyncDelay(new Long(delay.getSingle(event).getTicks_i()).intValue(),
                () -> walk(getNext(), event)
        );
        return null;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "async wait " + delay;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        delay = (Expression<Timespan>) expressions[0];
        return true;
    }
}
