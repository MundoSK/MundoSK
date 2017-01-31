package com.pie.tlatoani.Tablist;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 10/20/16.
 */
public class EffChangePlayerVisibility extends Effect {
    private boolean visible;
    private Expression<Tablist> tablistExpression;
    private Expression<Player> objects;

    @Override
    protected void execute(Event event) {
        Tablist tablist = tablistExpression.getSingle(event);
        if (visible)
            tablist.showPlayers(Arrays.asList(objects.getArray(event)));
        else
            tablist.hidePlayers(Arrays.asList(objects.getArray(event)));
    }

    @Override
    public String toString(Event event, boolean b) {
        return (visible ? "show " : "hide ") + objects + " in " + tablistExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        visible = parseResult.mark == 0;
        tablistExpression = (Expression<Tablist>) expressions[1];
        objects = (Expression<Player>) expressions[0];
        return true;
    }
}
