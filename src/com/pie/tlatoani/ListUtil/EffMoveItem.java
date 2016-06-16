package com.pie.tlatoani.ListUtil;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/11/16.
 */
public class EffMoveItem extends Effect {
    private ListUtil.Moveable expression;
    private Expression<Number> movement;
    private Integer direction;

    @Override
    protected void execute(Event event) {
        expression.move(event, movement.getSingle(event).intValue() * direction);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "move " + expression + " " + (direction == 1 ? "backward" : "forward") + " " + movement;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (expressions[0] instanceof ListUtil.Moveable) {
            expression = (ListUtil.Moveable) expressions[0];
        } else {
            Skript.error("The 'move items forwards/backwards' effect can only be used with the 'items %number% to %number%' and the 'item %number%' expressions!");
        }
        movement = (Expression<Number>) expressions[1];
        direction = parseResult.mark;
        return true;
    }
}
