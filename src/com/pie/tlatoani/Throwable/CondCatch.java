package com.pie.tlatoani.Throwable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class CondCatch extends Condition {
    private Expression<?> container;
    private List<Event> events = new ArrayList<Event>();

    public void putCatch(Event event, Throwable caught) {
        if (caught != null) {
            container.change(event, new Throwable[]{caught}, Changer.ChangeMode.SET);
            events.add(event);
        }
    }

    @Override
    public boolean check(Event event) {
        return events.contains(event);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "catch in %object%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        container = expressions[0];
        Class[] classes = container.acceptChange(Changer.ChangeMode.SET);
        if (!Arrays.asList(classes).contains(Throwable.class)) {
            Skript.error("The expression " + container + "cannot be set to an exception!");
            return false;
        }
        return true;
    }
}
