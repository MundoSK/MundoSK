package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Util.Registration;
import org.bukkit.event.Event;

import java.util.ArrayList;

/**
 * Created by Tlatoani on 7/5/16.
 */
public class ExprEventSpecificValue extends SimpleExpression<Object> {
    private static ArrayList<EventSpecificValue> specificValueArrayList = new ArrayList<>();
    private static ArrayList<String> patternList = new ArrayList<>();

    private EventSpecificValue eventSpecificValue;

    @Override
    protected Object[] get(Event event) {
        return eventSpecificValue.getValue(event);
    }

    @Override
    public boolean isSingle() {
        return eventSpecificValue.isSingle();
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return eventSpecificValue.getReturnType();
    }

    @Override
    public String toString(Event event, boolean b) {
        return eventSpecificValue.toString();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (ScriptLoader.isCurrentEvent(eventSpecificValue.getEventType())) {
            return true;
        }
        Skript.error("The expression '" + eventSpecificValue.toString() + "' cannot be used with this event!");
        return false;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode changeMode) {
        eventSpecificValue.change(event, changeMode, delta);
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (eventSpecificValue.isApplicableChangeMode(mode)) {
            return CollectionUtils.array(eventSpecificValue.getReturnType());
        }
        return null;
    }

    public static <R, E extends Event> void registerEventSpecificValue(
            String pattern,
            Class<? extends R> returnType, Class<? extends E> eventType,
            RawEventSpecificValue<R, E> rawEventSpecificValue,
            boolean isSingle, Changer.ChangeMode... changeModes) {
        EventSpecificValue<R, E> eventSpecificValue = new EventSpecificValue<R, E>() {
            @Override
            public R[] getValue(E event) {
                return rawEventSpecificValue.getValue(event);
            }

            @Override
            public Class<? extends R> getReturnType() {
                return returnType;
            }

            @Override
            public Class<? extends E> getEventType() {
                return eventType;
            }

            @Override
            public boolean isSingle() {
                return isSingle;
            }

            @Override
            public void change(E event, Changer.ChangeMode changeMode, Object[] delta) {
                rawEventSpecificValue.change(event, changeMode, delta);
            }

            @Override
            public boolean isApplicableChangeMode(Changer.ChangeMode changeMode) {
                for (int i = 0; i < changeModes.length; i++) {
                    if (changeMode == changeModes[i]) {
                        return true;
                    }
                }
                return false;
            }
        };
        specificValueArrayList.add(eventSpecificValue);
        patternList.add(pattern);
    }

    public static void register() {
        Registration.registerExpression(ExprEventSpecificValue.class, Object.class, ExpressionType.SIMPLE, patternList.toArray(new String[0]));
    }

    private interface EventSpecificValue<R, E extends Event> {

        R[] getValue(E event);

        Class<? extends R> getReturnType();

        Class<? extends E> getEventType();

        boolean isSingle();

        String toString();

        void change(E event, Changer.ChangeMode changeMode, Object[] delta);

        boolean isApplicableChangeMode(Changer.ChangeMode changeMode);
    }

    public interface RawEventSpecificValue<R, E extends Event> {

        R[] getValue(E event);

        default void change(E event, Changer.ChangeMode changeMode, Object[] delta) {
            throw new UnsupportedOperationException("Cannot be changed!");
        }

    }
}
