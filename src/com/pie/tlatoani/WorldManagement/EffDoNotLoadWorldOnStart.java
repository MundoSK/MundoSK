package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/2/16.
 */
public class EffDoNotLoadWorldOnStart extends Effect {
    Expression<String> worldname;

    @Override
    protected void execute(Event event) {
        UtilWorldLoader.removeCreator(worldname.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "don't load world %string% on start";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        worldname = (Expression<String>) expressions[0];
        return true;
    }
}
