package com.pie.tlatoani.CustomEvent;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class CondLastCustomEventCancelled extends SimpleExpression<Boolean> {
    private boolean negated;

    @Override
    protected Boolean[] get(Event event) {
        Optional<SkriptCustomEvent> customEventOptional = Optional.ofNullable(SkriptCustomEvent.lastCustomEvents.get(event));
        boolean cancelled = customEventOptional.map(SkriptCustomEvent::isCancelled).orElse(false);
        return new Boolean[]{cancelled != negated};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "last custom event " + (negated ? "wasn't" : "was") + " cancelled";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        negated = parseResult.mark == 1;
        return true;
    }
}
