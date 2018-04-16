package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import com.pie.tlatoani.Util.Skript.BaseEvent;
import org.bukkit.event.Event;

import java.util.HashMap;

/**
 * Created by Tlatoani on 2/24/17.
 */
public class CustomElementEvent extends BaseEvent {
    public final Event event;
    public final SkriptParser.ParseResult parseResult;
    private final HashMap<String, Expression<?>> exprs;
    private final HashMap<String, Object[]> evaluations = new HashMap<>();

    public CustomElementEvent(Event event, SkriptParser.ParseResult parseResult, HashMap<String, Expression<?>> exprs) {
        this.event = event;
        this.parseResult = parseResult;
        this.exprs = exprs;
    }

    public Object[] evalExpr(String name) {
        return evaluations.computeIfAbsent(name, __ -> exprs.remove(name).getArray(event));
    }
}
