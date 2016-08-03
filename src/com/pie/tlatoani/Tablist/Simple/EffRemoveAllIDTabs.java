package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/3/16.
 */
public class EffRemoveAllIDTabs extends Effect {
    private Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        TabListManager.getSimpleTabListForPlayer(playerExpression.getSingle(event)).clear();
    }

    @Override
    public String toString(Event event, boolean b) {
        return "delete all id tabs for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
