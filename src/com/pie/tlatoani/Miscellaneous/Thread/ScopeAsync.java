package com.pie.tlatoani.Miscellaneous.Thread;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/25/16.
 */
public class ScopeAsync extends CustomScope {
    @Override
    public String getString() {
        return "async";
    }

    @Override
    public void setScope() {
        last.setNext(null);
    }

    @Override
    public boolean go(Event event) {
        Mundo.scheduler.runTaskAsynchronously(Mundo.instance, new Runnable() {
            @Override
            public void run() {
                TriggerItem.walk(first, event);
            }
        });
        return false;
    }
}
