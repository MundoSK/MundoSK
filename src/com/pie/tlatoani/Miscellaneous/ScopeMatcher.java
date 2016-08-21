package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Expression;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/20/16.
 */
public class ScopeMatcher extends CustomScope {
    public Expression<Object> objectExpression;

    @Override
    public boolean go(Event event) {
        return true;
    }

    @Override
    public String getString() {
        return "match " + objectExpression;
    }

    @Override
    public boolean init() {
        objectExpression = (Expression<Object>) exprs[0];
        return true;
    }
}
