package com.pie.tlatoani.Miscellaneous.Hanging;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Checker;
import org.bukkit.event.Event;
import org.bukkit.event.hanging.HangingBreakEvent;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/21/17.
 */
public class EvtUnhang extends SkriptEvent {
    Optional<Literal<HangingBreakEvent.RemoveCause>> removeCauseOptional;

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        removeCauseOptional = Optional.ofNullable((Literal<HangingBreakEvent.RemoveCause>) literals[0]);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return removeCauseOptional.map(cause -> cause.check(event, new Checker<HangingBreakEvent.RemoveCause>() {
            @Override
            public boolean check(HangingBreakEvent.RemoveCause removeCause) {
                return removeCause == ((HangingBreakEvent) event).getCause();
            }
        })).orElse(true);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "unhang" + removeCauseOptional.map(cause -> " due to " + cause).orElse("");
    }
}
