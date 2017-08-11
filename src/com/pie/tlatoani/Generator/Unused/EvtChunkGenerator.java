package com.pie.tlatoani.Generator.Unused;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import com.pie.tlatoani.Generator.SkriptGenerator;
import com.pie.tlatoani.Generator.SkriptGeneratorManager;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class EvtChunkGenerator extends SelfRegisteringSkriptEvent {
    private String generatorID = null;
    private SkriptGenerator generator = null;

    public String getGeneratorID() {
        return generatorID;
    }

    public SkriptGenerator getGenerator() {
        return generator;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "world generator " + generatorID;
    }

    @Override
    public void register(Trigger trigger) {
        generator.trigger = trigger;
    }

    @Override
    public void unregister(Trigger trigger) {
        generator.trigger = null;
        generator.generation = null;
        generator.population = null;
    }

    @Override
    public void unregisterAll() {
        SkriptGeneratorManager.unregisterAllSkriptGenerators();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        generatorID = ((Literal<String>) literals[0]).getSingle();
        generator = SkriptGeneratorManager.getSkriptGenerator(generatorID);
        if (generator.trigger == null) {
            return true;
        }
        Skript.error("A world generator is already registered with the name '" + generatorID + "'!");
        return false;
    }
}
