package com.pie.tlatoani.Tablist.Player;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/13/17.
 */
public class EffClearPlayerModifications extends Effect {
    private Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        for (Player player : playerExpression.getArray(event)) {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (!tablist.getPlayerTablist().isPresent()) {
                tablist.setSupplementaryTablist(SimpleTablist::new);
            }
            tablist.getPlayerTablist().ifPresent(PlayerTablist::clearModifications);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "clear player tab modifications for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
