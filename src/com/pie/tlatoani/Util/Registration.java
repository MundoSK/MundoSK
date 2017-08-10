package com.pie.tlatoani.Util;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import ch.njol.yggdrasil.Fields;
import org.bukkit.event.Event;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.*;
import java.util.function.Function;

/**
 * Created by Tlatoani on 8/9/17.
 */
public class Registration {

    public static ArrayList<Object[]> ena = new ArrayList<>();
    public static ArrayList<String> enumNames = new ArrayList<>();
    public static ArrayList<Class<?>> enumClasses = new ArrayList<>();

    public static void registerEnumAllExpressions() {
        ArrayList<String> patterns = new ArrayList<>();
        for (String s : enumNames) {
            patterns.add("[all] " + s + "s");
        }
        Skript.registerExpression(ExprEnumValues.class, Object.class, ExpressionType.SIMPLE, patterns.toArray(new String[0]));
    }

    public static void registerEffect(Class<? extends Effect> effectClass, String... patterns) {
        Skript.registerEffect(effectClass, patterns);
    }

    public static <T> void registerExpression(Class<? extends Expression<T>> expressionClass, Class<T> type, ExpressionType expressionType, String... patterns) {
        Skript.registerExpression(expressionClass, type, expressionType, patterns);
    }

    public static void registerCondition(Class<? extends Condition> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }

    public static void registerEvent(String name, Class<? extends SkriptEvent> eventClass, Class<? extends Event> eventType, String... patterns) {
        Skript.registerEvent(name, eventClass, eventType, patterns);
    }

    public static void registerScope(Class<? extends CustomScope> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }

    public static <E extends Event, R> void registerEventValue(Class<E> tClass, Class<R> rClass, Function<E, R> function) {
        EventValues.registerEventValue(tClass, rClass, new Getter<R, E>() {
            @Override
            public R get(E event) {
                return function.apply(event);
            }
        }, 0);
    }

    public static Boolean classInfoSafe(Class c, String name) {
        return Classes.getExactClassInfo(c) == null && Classes.getClassInfoNoError(name) == null;
    }

    public static <T> ClassInfo<T> registerType(Class<T> type, String name, String... alternateNames) {
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(alternateNames));
        names.add(0, name);
        ClassInfo<T> result = new ClassInfo<T>(type, name).user(names.toArray(new String[0])).name(name).parser(new SimpleParser<T>() {
            @Override
            public T parse(String s, ParseContext parseContext) {
                return null;
            }
        });
        if (classInfoSafe(type, name)) {
            Classes.registerClass(result);
        }
        return result;
    }

    //Default pairing string names should be in uppercase
    public static <E> void registerEnum(Class<E> enumClass, String name, E[] values, Map.Entry<String, E>... defaultPairings) {
        if (!classInfoSafe(enumClass, name)) return;
        Classes.registerClass(new ClassInfo<E>(enumClass, name).user(new String[]{name}).name(name).parser(new Parser<E>() {
            private E[] enumValues = values;
            private Map.Entry<String, E>[] additionalPairings = defaultPairings;

            @Override
            public E parse(String s, ParseContext parseContext) {
                String upperCase = s.toUpperCase();
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getKey().equals(upperCase)) {
                        return additionalPairings[i].getValue();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].toString().equals(upperCase)) {
                        return values[i];
                    }
                }
                return null;
            }

            @Override
            public String toString(E e, int useless) {
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getValue() == e) {
                        return additionalPairings[i].getKey().toLowerCase();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == e) {
                        return values[i].toString().toLowerCase();
                    }
                }
                return null;
            }

            @Override
            public String toVariableNameString(E e) {
                return toString(e, 0);
            }

            @Override
            public String getVariableNamePattern() {
                return ".+";
            }
        }).serializer(new Serializer<E>() {
            private E[] enumValues = values;
            private Map.Entry<String, E>[] additionalPairings = defaultPairings;

            public E parse(String s) {
                String upperCase = s.toUpperCase();
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getKey().equals(upperCase)) {
                        return additionalPairings[i].getValue();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].toString().equals(upperCase)) {
                        return values[i];
                    }
                }
                return null;
            }

            public String toString(E e) {
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getValue() == e) {
                        return additionalPairings[i].getKey().toLowerCase();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == e) {
                        return values[i].toString().toLowerCase();
                    }
                }
                return null;
            }

            @Override
            public Fields serialize(E e) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("value", toString(e));
                return null;
            }

            @Override
            public void deserialize(E e, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }

            @Override
            public E deserialize(Fields fields) throws StreamCorruptedException {
                return parse((String) fields.getObject("value"));
            }
        }));
        Set<E> allValues = new HashSet<E>();
        allValues.addAll(Arrays.asList(values));
        for (Map.Entry<String, E> entry : defaultPairings) {
            allValues.add(entry.getValue());
        }
        ena.add(allValues.toArray(new Object[0]));
        enumNames.add(name);
        enumClasses.add(enumClass);
    }

    public static class ExprEnumValues extends SimpleExpression<Object> {
        private int whichEnum;

        @Override
        protected Object[] get(Event event) {
            return ena.get(whichEnum);
        }

        @Override
        public boolean isSingle() {
            return false;
        }

        @Override
        public Class<? extends Object> getReturnType() {
            return enumClasses.get(whichEnum);
        }

        @Override
        public String toString(Event event, boolean b) {
            return "all " + enumNames.get(whichEnum) + "s";
        }

        @Override
        public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
            whichEnum = i;
            return true;
        }
    }

    public static abstract class SimpleParser<T> extends Parser<T> {

        @Override
        public String toString(T t, int flags) {
            return t.toString();
        }

        @Override
        public String toVariableNameString(T t) {
            return toString(t, 0);
        }

        @Override
        public String getVariableNamePattern() {
            return ".+";
        }
    }
}
