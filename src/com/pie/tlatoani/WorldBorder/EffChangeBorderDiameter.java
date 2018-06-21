package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Config;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class EffChangeBorderDiameter extends Effect {
    private Expression<World> worldExpression;
    private Expression<Number> numberExpression;
    private Expression<Timespan> timespanExpression;
    private Changer.ChangeMode changeMode;

    @Override
    protected void execute(Event event) {
        World world = worldExpression.getSingle(event);
        WorldBorder border = world.getWorldBorder();
        double value = numberExpression.getSingle(event).doubleValue();
        long seconds = timespanExpression.getSingle(event).getMilliSeconds() / 1000;
        switch (changeMode) {
            case SET: border.setSize(value, seconds); break;
            case ADD: border.setSize(border.getSize() + value, seconds); break;
            case REMOVE: border.setSize(border.getSize() - value, seconds); break;
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        switch (changeMode) {
            case SET: return "set diameter of " + worldExpression + " to " + numberExpression + " over " + timespanExpression;
            case ADD: return "add " + numberExpression + " to diameter of " + worldExpression + " over " + timespanExpression;
            case REMOVE: return "subtract " + numberExpression + " from diameter of " + worldExpression + " over " + timespanExpression;
        }
        throw new IllegalStateException("Illegal ChangeMode: " + changeMode);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if ((parseResult.mark & 0b1000) == 0b1000) {
            if (Config.DISABLE_SIZE_SYNTAX.getCurrentValue()) {
                return false;
            }
            Skript.warning("The 'size' alias for border diameter will be removed in a future version. Please use 'diameter' instead.");
        }
        worldExpression = getWorldExpr(i, expressions);
        numberExpression = getNumberExpr(i, expressions);
        timespanExpression = getTimeExpr(i, expressions);
        changeMode = getChangeMode(i);
        return true;
    }

    public static Expression<World> getWorldExpr(int i, Expression<?>[] expressions) {
        if (i < 4) {
            return (Expression<World>) expressions[0];
        } else {
            return (Expression<World>) expressions[1];
        }
    }

    public static Expression<Number> getNumberExpr(int i, Expression<?>[] expressions) {
        if (i < 2) {
            return (Expression<Number>) expressions[1];
        } else if (i < 4) {
            return (Expression<Number>) expressions[2];
        } else {
            return (Expression<Number>) expressions[0];
        }
    }

    public static Expression<Timespan> getTimeExpr(int i, Expression<?>[] expressions) {
        if (i == 2 || i == 3) {
            return (Expression<Timespan>) expressions[1];
        } else {
            return (Expression<Timespan>) expressions[2];
        }
    }

    public static Changer.ChangeMode getChangeMode(int i) {
        if (i < 4) {
            return Changer.ChangeMode.SET;
        } else if (i < 6) {
            return Changer.ChangeMode.ADD;
        } else {
            return Changer.ChangeMode.REMOVE;
        }
    }
}
