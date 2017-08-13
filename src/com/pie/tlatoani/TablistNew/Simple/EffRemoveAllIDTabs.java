package com.pie.tlatoani.TablistNew.Simple;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.TablistNew.OldTablist;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
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
        for (Player player : playerExpression.getArray(event)) {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.clear();
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "delete all simple tabs for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
