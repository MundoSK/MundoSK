package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.WorldCreator.Dimension;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import com.pie.tlatoani.WorldManagement.WorldLoader.WorldLoader;
import org.bukkit.WorldType;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/25/17.
 */
public class EffCreateWorld extends Effect {
    private Expression<String> nameExpr;
    private Expression<Dimension> dimensionExpr;
    private Expression<String> seedExpr;
    private Expression<WorldType> typeExpr;
    private Expression<String> generatorExpr;
    private Expression<String> generatorSettingsExpr;
    private Expression<Boolean> structuresExpr;
    private boolean autoload;

    @Override
    protected void execute(Event event) {
        String name = nameExpr.getSingle(event);
        Logging.debug(this, "World Creation Name: " + name);
        Dimension dimension = Optional.ofNullable(dimensionExpr).map(expr -> expr.getSingle(event)).orElse(null);
        String seed = Optional.ofNullable(seedExpr).map(expr -> expr.getSingle(event)).orElse(null);
        WorldType type = Optional.ofNullable(typeExpr).map(expr -> expr.getSingle(event)).orElse(null);
        String generator = Optional.ofNullable(generatorExpr).map(expr -> expr.getSingle(event)).orElse(null);
        String generatorSettings = Optional.ofNullable(generatorSettingsExpr).map(expr -> expr.getSingle(event)).orElse(null);
        Boolean structures = Optional.ofNullable(structuresExpr).map(expr -> expr.getSingle(event)).orElse(null);
        WorldCreatorData creator = WorldCreatorData.withGeneratorID(name, dimension, seed, type, generator, generatorSettings, structures);
        creator.createWorld();
        if (autoload) {
            WorldLoader.setCreator(creator);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "create new world named " + nameExpr +
                ((dimensionExpr != null || typeExpr != null || seedExpr != null || generatorExpr != null || generatorSettingsExpr != null || structuresExpr != null)
                        ? " with"
                        + (dimensionExpr != null ? " dimension " + dimensionExpr : "")
                        + (seedExpr != null ? " seed " + seedExpr : "")
                        + (typeExpr != null ? " type " + typeExpr : "")
                        + (generatorExpr != null ? " generator " + generatorExpr : "")
                        + (generatorSettingsExpr != null ? " generator settings " + generatorSettingsExpr : "")
                        + (structuresExpr != null ? " structures " + structuresExpr : "")
                        : "");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        autoload = parseResult.mark == 1;
        nameExpr = (Expression<String>) expressions[0];
        dimensionExpr = (Expression<Dimension>) expressions[1];
        seedExpr = (Expression<String>) expressions[2];
        typeExpr = (Expression<WorldType>) expressions[3];
        generatorExpr = (Expression<String>) expressions[4];
        generatorSettingsExpr = (Expression<String>) expressions[5];
        structuresExpr = (Expression<Boolean>) expressions[6];
        return true;
    }
}
