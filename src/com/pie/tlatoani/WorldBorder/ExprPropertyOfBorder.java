package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Core.Static.Config;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by Tlatoani on 7/15/17.
 */
public class ExprPropertyOfBorder extends SimpleExpression<Number> {
    private BorderProperty borderProperty;
    private Expression<World> worldExpression;

    public enum BorderProperty {
        DIAMETER("diameter", 60000000, WorldBorder::getSize, Number::doubleValue, WorldBorder::setSize),
        DAMAGE_AMOUNT("damage amount", 0.2, WorldBorder::getDamageAmount, Number::doubleValue, WorldBorder::setDamageAmount),
        DAMAGE_BUFFER("damage buffer", 5, WorldBorder::getDamageBuffer, Number::doubleValue, WorldBorder::setDamageBuffer),
        WARNING_DISTANCE("warning distance", 5, WorldBorder::getWarningDistance, Number::intValue, WorldBorder::setWarningDistance),
        WARNING_TIME("warning time", 15, WorldBorder::getWarningTime, Number::intValue, WorldBorder::setWarningTime);

        public final String syntaxName;
        public final Double defaultValue;
        private final Function<WorldBorder, Number> getter;
        private final BiConsumer<WorldBorder, Number> setter;

        <T> BorderProperty(String syntaxName, double defaultValue, Function<WorldBorder, Number> getter, Function<Number, T> numConverter, BiConsumer<WorldBorder, T> setter) {
            this.syntaxName = syntaxName;
            this.defaultValue = defaultValue;
            this.getter = getter;
            this.setter = ((border, number) -> setter.accept(border, numConverter.apply(number)));
        }

        public Number get(WorldBorder border) {
            return getter.apply(border);
        }

        public void set(WorldBorder border, Number value) {
            setter.accept(border, value);
        }
    }

    @Override
    protected Number[] get(Event event) {
        WorldBorder border = worldExpression.getSingle(event).getWorldBorder();
        return new Number[]{borderProperty.get(border)};
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
        if ((parseResult.mark & 0b1000) == 0b1000) {
            if (Config.DISABLE_SIZE_SYNTAX.getCurrentValue()) {
                return false;
            }
            Skript.warning("The 'size' alias for border diameter is not recommended as it is vague and may cause conflicts. " +
                    "Please use 'diameter' instead. " +
                    "If you were not trying to use a border syntax here, go to MundoSK's config and set the 'border_disable_size_syntax' option to true. " +
                    "Make sure any uses of 'size' for border diameter are changed to 'diameter' before you do this.");
        }
        borderProperty = BorderProperty.values()[parseResult.mark & 0b0111];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode changeMode) {
        WorldBorder border = worldExpression.getSingle(event).getWorldBorder();
        if (changeMode == Changer.ChangeMode.SET || changeMode == Changer.ChangeMode.ADD || changeMode == Changer.ChangeMode.REMOVE) {
            Number value = (Number) delta[0];
            if (changeMode == Changer.ChangeMode.ADD) {
                value = borderProperty.get(border).doubleValue() + value.doubleValue();
            } else if (changeMode == Changer.ChangeMode.REMOVE) {
                value = borderProperty.get(border).doubleValue() + value.doubleValue();
            }
            borderProperty.set(border, value);
        } else if (changeMode == Changer.ChangeMode.RESET) {
            borderProperty.set(border, borderProperty.defaultValue);
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
