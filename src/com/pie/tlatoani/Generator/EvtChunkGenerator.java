package com.pie.tlatoani.Generator;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class EvtChunkGenerator extends SkriptEvent {
    private String generatorID = null;

    public TriggerItem generation;
    public TriggerItem creation;
    public TriggerItem loading;

    public String getGeneratorID() {
        return generatorID;
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        generatorID = ((Literal<String>) literals[0]).getSingle();
        if (SkriptGeneratorManager.addID(generatorID)) {
            return true;
        }
        Skript.error("A world generator is already registered with the name '" + generatorID + "'!");
        return false;
    }

    @Override
    public boolean check(Event event) {
        return false;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "world generator " + generatorID;
    }
}
