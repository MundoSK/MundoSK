package com.pie.tlatoani.TablistNew.Array;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.TablistNew.OldTablist;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/17/17.
 */
public class EffMinimizeTablist extends Effect {
    private Expression<OldTablist> tablistExpression;

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
        tablistExpression = (Expression<OldTablist>) expressions[0];
        return true;
    }
}
