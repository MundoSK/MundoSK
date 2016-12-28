package com.pie.tlatoani.Miscellaneous.Thread;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class ScopeAsync extends CustomScope {
    Expression<Timespan> delay;

    public ScopeAsync() {
        canStandFree = true;
    }

    @Override
    public String getString() {
        return "async" + (delay == null ? "" : " in " + delay);
    }

    @Override
    public void setScope() {
        last.setNext(null);
    }

    @Override
    public boolean go(Event event) {
        if (delay == null) {
            Mundo.scheduler.runTaskAsynchronously(Mundo.instance, new Runnable() {
                @Override
                public void run() {
                    TriggerItem.walk(first, event);
                }
            });
        } else {
            Mundo.scheduler.runTaskLaterAsynchronously(Mundo.instance, new Runnable() {
                @Override
                public void run() {
                    TriggerItem.walk(first, event);
                }
            }, delay.getSingle(event).getTicks_i());
        }
        return false;
    }

    @Override
    public boolean init() {
        delay = (Expression<Timespan>) exprs[0];
        return true;
    }

    @Override
    public TriggerItem walk(Event event) {
        Mundo.async(new Runnable() {
            @Override
            public void run() {
                TriggerItem.walk(getNext(), event);
            }
        });
        return null;
    }
}
