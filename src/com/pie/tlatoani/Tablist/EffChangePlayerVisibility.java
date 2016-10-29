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
    private Expression<Player> targets;
    private Expression<Player> objects;

    @Override
    protected void execute(Event event) {
        for (Player target : targets.getArray(event))
            for (Player object : objects.getArray(event))
                if (visible)
                    TabListManager.showPlayer(object, target);
                else
                    TabListManager.hidePlayer(object, target);
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        visible = parseResult.mark == 0;
        targets = (Expression<Player>) expressions[1];
        objects = (Expression<Player>) expressions[0];
        return true;
    }
}
