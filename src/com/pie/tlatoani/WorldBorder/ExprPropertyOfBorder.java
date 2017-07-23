package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class ExprPropertyOfBorder extends SimpleExpression<Number> {
    private BorderProperty borderProperty;
    private Expression<World> worldExpression;

    public enum BorderProperty {
        SIZE("size"),
        DAMAGE_AMOUNT("damage amount"),
        DAMAGE_BUFFER("damage buffer"),
        WARNING_DISTANCE("warning distance"),
        WARNING_TIME("warning time");

        public final String syntaxName;

        BorderProperty(String syntaxName) {
            this.syntaxName = syntaxName;
        }
    }

    public static Number getProperty(WorldBorder border, BorderProperty borderProperty) {
        switch (borderProperty) {
            case SIZE: return border.getSize();
            case DAMAGE_AMOUNT: return border.getDamageAmount();
            case DAMAGE_BUFFER: return border.getDamageBuffer();
            case WARNING_DISTANCE: return border.getWarningDistance();
            case WARNING_TIME: return border.getWarningTime();
        }
        throw new IllegalArgumentException("Illegal BorderProperty: " + borderProperty);
    }

    public static void setProperty(WorldBorder border, BorderProperty borderProperty, Number value) {
        switch (borderProperty) {
            case SIZE: border.setSize(value.doubleValue()); return;
            case DAMAGE_AMOUNT: border.setDamageAmount(value.doubleValue()); return;
            case DAMAGE_BUFFER: border.setDamageBuffer(value.doubleValue()); return;
            case WARNING_DISTANCE: border.setWarningDistance(value.intValue()); return;
            case WARNING_TIME: border.setWarningTime(value.intValue()); return;
        }
        throw new IllegalArgumentException("Illegal BorderProperty: " + borderProperty);
    }

    public static void resetProperty(WorldBorder border, BorderProperty borderProperty) {
        switch (borderProperty) {
            case SIZE: border.setSize(29999984); return;
            case DAMAGE_AMOUNT: border.setDamageAmount(0.2); return;
            case DAMAGE_BUFFER: border.setDamageBuffer(5); return;
            case WARNING_DISTANCE: border.setWarningDistance(5); return;
            case WARNING_TIME: border.setWarningTime(15); return;
        }
        throw new IllegalArgumentException("Illegal BorderProperty: " + borderProperty);
    }

    @Override
    protected Number[] get(Event event) {
        WorldBorder border = worldExpression.getSingle(event).getWorldBorder();
        return new Number[]{getProperty(border, borderProperty)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return borderProperty.syntaxName + " of " + worldExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        worldExpression = (Expression<World>) expressions[0];
        borderProperty = BorderProperty.values()[parseResult.mark];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode changeMode) {
        WorldBorder border = worldExpression.getSingle(event).getWorldBorder();
        if (changeMode == Changer.ChangeMode.SET || changeMode == Changer.ChangeMode.ADD || changeMode == Changer.ChangeMode.REMOVE) {
            Number value = (Number) delta[0];
            if (changeMode == Changer.ChangeMode.ADD) {
                Number original = getProperty(border, borderProperty);
                if (original instanceof Integer) {
                    value = (Integer) original + value.intValue();
                } else {
                    value = (Double) original + value.doubleValue();
                }
            } else if (changeMode == Changer.ChangeMode.REMOVE) {
                Number original = getProperty(border, borderProperty);
                if (original instanceof Integer) {
                    value = (Integer) original - value.intValue();
                } else {
                    value = (Double) original - value.doubleValue();
                }
            }
            setProperty(border, borderProperty, value);
        } else if (changeMode == Changer.ChangeMode.RESET) {
            resetProperty(border, borderProperty);
        } else {
            throw new IllegalArgumentException("Illegal ChangeMode: " + changeMode);
        }
    }

    @Override
    public Class[] acceptChange(Changer.ChangeMode changeMode) {
        switch (changeMode) {
            case SET:
            case ADD:
            case REMOVE:
            case RESET:
                return CollectionUtils.array(Number.class);
            default:
                return null;
        }
    }
}
