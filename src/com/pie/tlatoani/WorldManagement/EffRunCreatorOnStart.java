package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/2/16.
 */
public class EffRunCreatorOnStart extends Effect {
    Expression<WorldCreator> creatorExpression;

    @Override
    protected void execute(Event event) {
        UtilWorldLoader.setCreator(creatorExpression.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "run %creator% on start";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        creatorExpression = (Expression<WorldCreator>) expressions[0];
        return true;
    }
}
