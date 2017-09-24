package com.pie.tlatoani.CustomEventNew;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/12/17.
 */
public class ScopeCustomEvent extends SelfRegisteringSkriptEvent {

    @Override
    public void register(Trigger trigger) {

    }

    @Override
    public void unregister(Trigger trigger) {

    }

    @Override
    public void unregisterAll() {

    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return false;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }
}
