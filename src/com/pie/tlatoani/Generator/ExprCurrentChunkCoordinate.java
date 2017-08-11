package com.pie.tlatoani.Generator;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class ExprCurrentChunkCoordinate extends SimpleExpression<Number> {
    private boolean isX;

    @Override
    protected Number[] get(Event event) {
        //SkriptGeneratorEvent skriptGeneratorEvent = (SkriptGeneratorEvent) event;
        GeneratorEvent.Generation generatorEvent = (GeneratorEvent.Generation) event;
        return new Number[]{isX ? generatorEvent.x : generatorEvent.z};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "current " + (isX ? "x" : "z");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        isX = i == 0;
        if (ScriptLoader.isCurrentEvent(GeneratorEvent.Generation.class)) {
            return true;
        }
        Skript.error("The 'current (x|z)' expression can only be used in the 'generation' section of a custom world generator!");
        return false;
    }
}
