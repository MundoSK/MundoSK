package com.pie.tlatoani.Tablist.Group;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/22/18.
 */
public class EffAddToTablistGroup extends Effect {
    private Expression<String> nameExpression;
    private Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        String name = nameExpression.getSingle(event);
        Player[] players = playerExpression.getArray(event);
        if (name != null && players != null) {
            TablistGroup group = TablistManager.getTablistGroup(name);
            for (Player player : players) {
                if (player.isOnline()) {
                    group.add(player);
                }
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "add " + playerExpression + " to tablist group " + nameExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        nameExpression = (Expression<String>) expressions[1];
        return true;
    }
}
