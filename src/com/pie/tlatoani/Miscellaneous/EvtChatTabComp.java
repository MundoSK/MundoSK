package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

/**
 * Created by Tlatoani on 6/16/16.
 */
public class EvtChatTabComp extends SkriptEvent {

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return event instanceof PlayerChatTabCompleteEvent;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "chat tab complete";
    }
}
