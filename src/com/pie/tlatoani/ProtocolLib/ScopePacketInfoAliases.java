package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import com.pie.tlatoani.Util.MundoEventScope;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 10/15/17.
 */
public class ScopePacketInfoAliases extends MundoEventScope {

    @Override
    protected void afterInit() {

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
