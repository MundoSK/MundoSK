package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class EffCreateWorld extends Effect {
    private Expression<String> name;
    private Expression<World.Environment> env;
    private Expression<String> seed;
    private Expression<WorldType> type;
    private Expression<String> gen;
    private Expression<String> genset;
    private Expression<Boolean> struct;

    @Override
    protected void execute(Event arg0) {
        String z = name.getSingle(arg0);
        WorldCreator x = new WorldCreator(z);
        x.generateStructures(true);
        if (seed != null && seed.getSingle(arg0).length() > 0) {
            x.seed(Long.parseLong(seed.getSingle(arg0)));
        }
        if (gen != null) x.generator(gen.getSingle(arg0));
        if (genset != null) x.generatorSettings(genset.getSingle(arg0));
        if (struct != null) x.generateStructures(struct.getSingle(arg0));
        if (env != null) x.environment(env.getSingle(arg0));
        if (type != null) x.type(type.getSingle(arg0));
        x.createWorld();
    }

    @Override
    public String toString(Event event, boolean b) {
        return "create world";
    }

    @Override
    public boolean init(Expression<?>[] expr, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        name = (Expression<String>) expr[0];
        env = (Expression<World.Environment>) expr[1];
        seed = (Expression<String>) expr[2];
        type = (Expression<WorldType>) expr[3];
        gen = (Expression<String>) expr[4];
        genset = (Expression<String>) expr[5];
        struct = (Expression<Boolean>) expr[6];
        return true;
    }
}
