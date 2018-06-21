package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/26/16.
 */
public class EffSleep extends Effect {
    private Expression<Boolean> condition;
    private boolean until;

    @Override
    protected TriggerItem walk(Event event) {
        if (condition.getSingle(event) == until) {
            walk(getNext(), event);
        } else {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logging.reportException(this, e);
            }
            walk(event);
        }
        return null;
    }

    @Override
    protected void execute(Event event) {
    }

    @Override
    public String toString(Event event, boolean b) {
        return "sleep " + (until ? "until" : "while") + " " + condition;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        condition = (Expression<Boolean>) expressions[0];
        until = parseResult.mark == 0;
        return true;
    }
}
