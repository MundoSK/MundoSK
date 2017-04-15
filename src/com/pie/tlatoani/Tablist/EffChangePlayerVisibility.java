package com.pie.tlatoani.Tablist;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 10/20/16.
 */
public class EffChangePlayerVisibility extends Effect {
    private boolean visible;
    private Expression<OldTablist> tablistExpression;
    private Expression<Player> playerExpression;
    private Expression<Player> objectsExpression;

    @Override
    protected void execute(Event event) {
        OldTablist oldTablist = tablistExpression != null ? tablistExpression.getSingle(event) : OldTablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        Player[] objects = objectsExpression.getArray(event);
        if (visible) {
            for (Player object : objects) {
                oldTablist.showTab(object, player);
            }
        } else {
            for (Player object : objects) {
                OldTablist.PlayerOldTab tab = oldTablist.getTab(object);
                if (tab != null) {
                    tab.hideFor(player);
                }
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return (visible ? "show " : "hide ") + objectsExpression + " in " + tablistExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        visible = parseResult.mark == 0;
        tablistExpression = (Expression<OldTablist>) expressions[1];
        playerExpression = (Expression<Player>) expressions[2];
        objectsExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
