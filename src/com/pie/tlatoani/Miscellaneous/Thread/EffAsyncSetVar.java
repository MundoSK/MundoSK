package com.pie.tlatoani.Miscellaneous.Thread;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 9/23/17.
 */
public class EffAsyncSetVar extends Effect {
    private Variable<?> variable;
    private Expression<?> value;

    @Override
    protected TriggerItem walk(Event event) {
        Scheduling.async(() -> {
            Object[] delta = value.getArray(event);
            Scheduling.sync(() -> {
                if (delta == null || delta.length > 0) {
                    variable.change(event, delta, Changer.ChangeMode.SET);
                }
                TriggerItem.walk(getNext(), event);
            });
        });
        return null;
    }

    @Override
    protected void execute(Event event) {}

    @Override
    public String toString(Event event, boolean b) {
        return "async set " + variable + " to " + value;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!(expressions[0] instanceof Variable)) {
            Skript.error("The 'async set' effect should only be used to set variables!");
            return false;
        }
        variable = (Variable) expressions[0];
        value = expressions[1];
        if (!value.isSingle() && !variable.isList()) {
            Skript.error("You cannot set '" + variable + "' to a list!");
            return false;
        }
        return true;
    }
}
