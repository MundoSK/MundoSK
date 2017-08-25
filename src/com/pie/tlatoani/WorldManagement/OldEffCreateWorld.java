package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import com.pie.tlatoani.WorldCreator.Dimension;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class OldEffCreateWorld extends Effect {
    private Expression<String> name;
    private Expression<Dimension> dim;
    private Expression<String> seed;
    private Expression<WorldType> type;
    private Expression<String> gen;
    private Expression<String> genset;
    private Expression<Boolean> struct;

    @Override
    protected void execute(Event event) {
        String z = name.getSingle(event);
        WorldCreator x = new WorldCreator(z);
        x.generateStructures(true);
        if (seed != null && seed.getSingle(event).length() > 0) {
            x.seed(Long.parseLong(seed.getSingle(event)));
        }
        if (gen != null) x.generator(ChunkGeneratorWithID.getGenerator(gen.getSingle(event)));
        if (genset != null) x.generatorSettings(genset.getSingle(event));
        if (struct != null) x.generateStructures(struct.getSingle(event));
        if (dim != null) x.environment(dim.getSingle(event).toEnvironment());
        if (type != null) x.type(type.getSingle(event));
        x.createWorld();
    }

    @Override
    public String toString(Event event, boolean b) {
        return "create world";
    }

    @Override
    public boolean init(Expression<?>[] expr, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        name = (Expression<String>) expr[0];
        dim = (Expression<Dimension>) expr[1];
        seed = (Expression<String>) expr[2];
        type = (Expression<WorldType>) expr[3];
        gen = (Expression<String>) expr[4];
        genset = (Expression<String>) expr[5];
        struct = (Expression<Boolean>) expr[6];
        return true;
    }
}
