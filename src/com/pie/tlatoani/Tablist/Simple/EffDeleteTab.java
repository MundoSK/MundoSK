package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffDeleteTab extends Effect {
    private Expression<String> id;
    private Expression<Tablist> tablistExpression;
    private Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        Tablist tablist = tablistExpression != null ? tablistExpression.getSingle(event) : Tablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        tablist.simpleTablist.deleteTab(player, id.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "delete tab id " + id + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        tablistExpression = (Expression<Tablist>) expressions[1];
        playerExpression = (Expression<Player>) expressions[2];
        return true;
    }
}
