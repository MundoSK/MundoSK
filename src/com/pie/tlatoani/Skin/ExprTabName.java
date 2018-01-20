package com.pie.tlatoani.Skin;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/20/18.
 */
public class ExprTabName extends SimpleExpression<String> {
    @Override
    protected String[] get(Event event) {
        return new String[0];
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return null;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Skript.error("The 'mundosk tablist name of %player%' expression was removed in MundoSK 1.8.3. "
                + "Please use the Displayed Name of Player Tab expression using 'all players' instead (do '/mundosk doc displayed name of player tab' for more info)");
        return false;
    }
}
