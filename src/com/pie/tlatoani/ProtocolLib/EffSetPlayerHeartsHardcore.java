package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/27/16.
 */
public class EffSetPlayerHeartsHardcore extends Effect {
    @Override
    protected void execute(Event event) {
        UtilPlayerLoginPacketEvent castevent = (UtilPlayerLoginPacketEvent) event;
        castevent.setHardCoreStyle();
    }

    @Override
    public String toString(Event event, boolean b) {
        return "make player's hearts hardcore style";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(UtilPlayerLoginPacketEvent.class)) {
            Skript.error("Cannot use 'make player's hearts hardcore style' outside of player login packet event");
            return false;
        }
        return true;
    }
}
