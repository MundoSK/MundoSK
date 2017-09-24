package com.pie.tlatoani.Miscellaneous.Random;

import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Random;

/**
 * Created by Tlatoani on 7/25/16.
 */
public class ExprNewRandom extends SimpleExpression<Random> implements DefaultExpression<Random> {
    Expression<Number> seed;
    boolean isDefault = false;

    public ExprNewRandom setDefault() {
        isDefault = true;
        return this;
    }

    @Override
    protected Random[] get(Event event) {
        if (seed == null )
            return new Random[]{new Random()};
        else
            return new Random[]{new Random(seed.getSingle(event).longValue())};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Class<? extends Random> getReturnType() {
        return Random.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "new random" + (seed == null ? "" : " from seed " + seed);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        seed = (Expression<Number>) expressions[0];
        return true;
    }

    @Override
    public boolean init() {
        seed = null;
        return true;
    }
}
