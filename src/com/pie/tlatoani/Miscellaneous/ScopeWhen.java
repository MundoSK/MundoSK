package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/26/16.
 */
public class ScopeWhen extends CustomScope {
    private Expression<Boolean> condition;

    @Override
    public String getString() {
        return "when " + condition;
    }

    @Override
    public boolean init() {
        condition = (Expression<Boolean>) exprs[0];
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
            Mundo.sync(1, () -> go(event));
        }
        return false;
    }
}
