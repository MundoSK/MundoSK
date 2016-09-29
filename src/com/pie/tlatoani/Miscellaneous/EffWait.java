package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class EffWait extends Effect {
    private Expression<Boolean> condition;
    private boolean until;

    @Override
    protected TriggerItem walk(Event event) {
        if (condition.getSingle(event) == until) {
            walk(getNext(), event);
        } else {
            Mundo.scheduler.runTaskLater(Mundo.instance, new Runnable() {
                @Override
                public void run() {
                    walk(event);
                }
            }, 1);
        }
        return null;
    }

    @Override
    protected void execute(Event event) {}

    @Override
    public String toString(Event event, boolean b) {
        return "wait " + (until ? "until" : "while") + " " + condition;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        condition = (Expression<Boolean>) expressions[0];
        until = parseResult.mark == 0;
        return true;
    }
}
