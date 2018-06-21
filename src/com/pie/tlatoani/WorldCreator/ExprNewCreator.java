package com.pie.tlatoani.WorldCreator;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.WorldType;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprNewCreator extends SimpleExpression<WorldCreatorData> {
    private Expression<String> nameExpr;
    private Expression<Dimension> dimensionExpr;
    private Expression<String> seedExpr;
    private Expression<WorldType> typeExpr;
    private Expression<String> generatorExpr;
    private Expression<String> generatorSettingsExpr;
    private Expression<Boolean> structuresExpr;
    
    @Override
    protected WorldCreatorData[] get(Event event) {
        String name = Optional.ofNullable(nameExpr).map(expr -> expr.getSingle(event)).orElse(null);
        Logging.debug(this, "Creator Name: " + name);
        Dimension dimension = Optional.ofNullable(dimensionExpr).map(expr -> expr.getSingle(event)).orElse(null);
        String seed = Optional.ofNullable(seedExpr).map(expr -> expr.getSingle(event)).orElse(null);
        WorldType type = Optional.ofNullable(typeExpr).map(expr -> expr.getSingle(event)).orElse(null);
        String generator = Optional.ofNullable(generatorExpr).map(expr -> expr.getSingle(event)).orElse(null);
        String generatorSettings = Optional.ofNullable(generatorSettingsExpr).map(expr -> expr.getSingle(event)).orElse(null);
        Boolean structures = Optional.ofNullable(structuresExpr).map(expr -> expr.getSingle(event)).orElse(null);
        return new WorldCreatorData[]{WorldCreatorData.withGeneratorID(name, dimension, seed, type, generator, generatorSettings, structures)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WorldCreatorData> getReturnType() {
        return WorldCreatorData.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "world creator" +
                (nameExpr != null ? " named " + nameExpr : "") +
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
