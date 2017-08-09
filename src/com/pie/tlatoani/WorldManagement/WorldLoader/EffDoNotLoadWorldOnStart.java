package com.pie.tlatoani.WorldManagement.WorldLoader;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.WorldManagement.WorldLoader.UtilWorldLoader;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/2/16.
 */
@Deprecated
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
        Skript.error("The 'don't load world %string% on start' effect will be removed in a future version, please use the 'creators to load on start' and 'creator %string to load on start' expressions instead (View MundoSK's documentation for more info)");
        return true;
    }
}
