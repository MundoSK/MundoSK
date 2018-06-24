package com.pie.tlatoani.Tablist.Player;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/13/17.
 */
public class EffClearPlayerModifications extends Effect {
    private TablistProvider tablistProvider;

    @Override
    protected void execute(Event event) {
        for (Tablist tablist : tablistProvider.get(event)) {
            if (!tablist.getPlayerTablist().isPresent()) {
                tablist.setSupplementaryTablist(SimpleTablist::new);
            }
            tablist.getPlayerTablist().ifPresent(PlayerTablist::clearModifications);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("clear player tab modifications [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        return true;
    }
}
