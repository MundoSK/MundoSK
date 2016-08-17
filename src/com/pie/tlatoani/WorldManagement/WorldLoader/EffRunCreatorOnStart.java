package com.pie.tlatoani.WorldManagement.WorldLoader;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.WorldManagement.WorldLoader.UtilWorldLoader;
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
        Skript.warning("The 'run %creator% on start' effect will be removed in a future version, please use the 'creators to load on start' and 'creator %string to load on start' expressions instead (View MundoSK's documentation for more info)");
        return true;
    }
}
