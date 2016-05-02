package com.pie.tlatoani.Util;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

/**
 * Created by Tlatoani on 5/1/16.
 */
public class CondRecheck extends Condition {
    private Condition condition = null;

    @Override
    public boolean check(Event event) {
        if (condition != null) {
            return condition.check(event);
        } else {
            Boolean nula = null;
            return nula;
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "recheck";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public TriggerItem setParent(final @Nullable TriggerSection parent) {
        super.parent = parent;
        TriggerSection currentParent = parent;
        Boolean cont = true;
        while (cont) {
            Mundo.debug(this, "currentParent: " + currentParent);
            if (currentParent instanceof While) {
                cont = false;
                try {
                    condition = (Condition) CustomScope.whilecondition.get(currentParent);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (currentParent instanceof Trigger) {
                cont = false;
                Skript.error("'recheck' needs to be written within a while loop!");
            } else {
                currentParent = currentParent.getParent();
            }
        }
        return this;
    }
}
