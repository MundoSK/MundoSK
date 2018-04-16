package com.pie.tlatoani.Tablist.Group;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/1/18.
 */
public class EffEmptyGroup extends Effect {
    private Expression<String> nameExpression;

    @Override
    protected void execute(Event event) {
        String name = nameExpression.getSingle(event);
        if (name != null) {
            TablistManager.getTablistGroup(name).clear();
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "empty tablist group " + nameExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        nameExpression = (Expression<String>) expressions[0];
        return true;
    }
}
