package com.pie.tlatoani.CustomEventNew;

import ch.njol.skript.classes.ClassInfo;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.ZExperimental.SyntaxPiece.SyntaxPiece;

import java.util.Optional;

/**
 * Created by Tlatoani on 1/14/18.
 */
public class CustomEventInfo {
    public final String name;
    public final SyntaxPiece syntax;
    public final boolean cancellable;
    public final ImmutableList<EventValue> eventValues;
    public final ImmutableList<SpecificExpression> specificExpressions;
    public final Class<? extends SkriptCustomEvent> eventClass;

    public CustomEventInfo(
            String name,
            SyntaxPiece syntax,
            boolean cancellable,
            ImmutableList<EventValue> eventValues,
            ImmutableList<SpecificExpression> specificExpressions,
            Class<? extends SkriptCustomEvent> eventClass) {
        this.name = name;
        this.syntax = syntax;
        this.cancellable = cancellable;
        this.eventValues = eventValues;
        this.specificExpressions = specificExpressions;
        this.eventClass = eventClass;
    }

    public static class EventValue {
        public final ClassInfo<?> type;
        public final Optional<Optional<?>> defaultValue;
        public final int index;

        public EventValue(ClassInfo<?> type, Optional<Optional<?>> defaultValue, int index) {
            this.type = type;
            this.defaultValue = defaultValue;
            this.index = index;
        }
    }

    public static class SpecificExpression {
        public final String name;
        public final ClassInfo<?> type;
        public final boolean single;
        public final boolean settable;
        public final Optional<Optional<?>> defaultValue; //Optional.empty() if there is no default value, Optional.of(Optional.empty()) if it is not set by default
        public final int index;

        public SpecificExpression(String name, ClassInfo<?> type, boolean single, boolean settable, Optional<Optional<?>> defaultValue, int index) {
            this.name = name;
            this.type = type;
            this.single = single;
            this.settable = settable;
            this.defaultValue = defaultValue;
            this.index = index;
        }
    }
}
