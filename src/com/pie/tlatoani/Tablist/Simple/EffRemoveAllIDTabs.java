package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.OldTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/3/16.
 */
public class EffRemoveAllIDTabs extends Effect {
    private Expression<OldTablist> tablistExpression;
    private Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        OldTablist oldTablist = tablistExpression != null ? tablistExpression.getSingle(event) : OldTablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        oldTablist.simpleTablist.clear(player);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "delete all id personalTabs for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistExpression = (Expression<OldTablist>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }
}
