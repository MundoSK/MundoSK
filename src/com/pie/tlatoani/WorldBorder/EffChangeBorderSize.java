package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class EffChangeBorderSize extends Effect {
    private Expression<World> worldExpression;
    private Expression<Number> numberExpression;
    private Expression<Timespan> timespanExpression;
    private Changer.ChangeMode changeMode;

    @Override
    protected void execute(Event event) {

    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    public static Changer.ChangeMode getByMark(int mark) {
        switch (mark) {
            case 0: return Changer.ChangeMode.SET;
            case 1: return Changer.ChangeMode.ADD;
            case -1: return Changer.ChangeMode.REMOVE;
        }
        throw new IllegalArgumentException("Illegal mark: " + mark);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        worldExpression = Optional.ofNullable((Expression<World>) expressions[0]).orElse((Expression<World>) expressions[1]);
        changeMode = getByMark(parseResult.mark);
        return true;
    }
}
