package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/17/17.
 */
public class EffMinimizeTablist extends Effect {
    private Expression<Tablist> tablistExpression;

    @Override
    protected void execute(Event event) {
        tablistExpression.getSingle(event).arrayTablist.setColumns(0);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "maximize array tablist " + tablistExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistExpression = (Expression<Tablist>) expressions[0];
        return true;
    }
}
