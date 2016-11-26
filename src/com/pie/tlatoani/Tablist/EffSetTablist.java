package com.pie.tlatoani.Tablist;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class EffSetTablist extends Effect {
    private Expression<Player> playersExpression;
    private Expression<Tablist> tablistExpression;


    @Override
    protected void execute(Event event) {
        Tablist.setTablistForPlayer(Arrays.asList(playersExpression.getArray(event)), tablistExpression.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "set tablist of " + playersExpression + " to " + tablistExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playersExpression = (Expression<Player>) expressions[0];
        tablistExpression = (Expression<Tablist>) expressions[1];
        return true;
    }
}
