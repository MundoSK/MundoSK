package com.pie.tlatoani.Registration;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.*;
import ch.njol.skript.classes.Comparator;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.*;
import ch.njol.skript.util.Getter;
import com.pie.tlatoani.Util.*;
import org.bukkit.event.Event;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Tlatoani on 8/9/17.
 */
public final class Registration {

    //public static ArrayList<Object[]> ena = new ArrayList<>();
    //public static ArrayList<String> enumNames = new ArrayList<>();
    //public static ArrayList<Class<?>> enumClasses = new ArrayList<>();

    private static String currentCategory = null;

    public static void register(String category, Runnable registerer) {
        String prevCategory = currentCategory;
        currentCategory = category;
        registerer.run();
        currentCategory = prevCategory;
    }

    /*public static void registerEnumAllExpressions() {
        ArrayList<String> patterns = new ArrayList<>();
        for (String enumName : enumNames) {
            patterns.add("[all] " + enumName + "s");
        }
        Skript.registerExpression(ExprEnumValues.class, Object.class, ExpressionType.SIMPLE, patterns.toArray(new String[0]));
    }*/

    public static DocumentationBuilder.Effect registerEffect(Class<? extends Effect> effectClass, String... patterns) {
        Skript.registerEffect(effectClass, patterns);
        return new DocumentationBuilder.Effect(currentCategory, patterns);
    }

    public static <T> DocumentationBuilder.Expression registerExpression(Class<? extends Expression<T>> expressionClass, Class<T> type, ExpressionType expressionType, String... patterns) {
        Skript.registerExpression(expressionClass, type, expressionType, patterns);
        return new DocumentationBuilder.Expression(currentCategory, patterns, type);
    }

    public static <T> DocumentationBuilder.Expression registerPropertyExpression(Class<? extends Expression<T>> expressionClass, Class<T> type, String possessorType, String... properties) {
        ArrayList<String> patternList = new ArrayList<>(properties.length);
        ArrayList<String> propertyList = new ArrayList<>(properties.length);
        for (int i = 0; i < properties.length; i++) {
            String property = properties[i];
            if (property.contains("%")) {
                patternList.add(property.replace("%", "%" + possessorType + "%"));
                propertyList.add(property);
            } else {
                patternList.add("[the] " + property + " of %" + possessorType + "%");
                patternList.add("%" + possessorType + "%'[s] " + property);
                propertyList.add(property);
                propertyList.add(property);
            }
        }
        String[] patterns = patternList.toArray(new String[0]);
        Skript.registerExpression(expressionClass, type, ExpressionType.PROPERTY, patterns);
        if (MundoPropertyExpression.class.isAssignableFrom(expressionClass)) {
            MundoPropertyExpression.registerPropertyExpressionInfo((Class<? extends MundoPropertyExpression>) expressionClass, type, propertyList);
        }
        return new DocumentationBuilder.Expression(currentCategory, patterns, type);
    }

    public static <T, E extends Event> DocumentationBuilder.Expression registerEventSpecificExpression(Class<? extends EventSpecificExpression<T, E>> expressionClass, Class<T> type, Class<E> event, String invalidEventError, String... patterns) {
        Skript.registerExpression(expressionClass, type, ExpressionType.SIMPLE, patterns);
        EventSpecificExpression.registerEventSpecificExpression(expressionClass, type, event, patterns[0], invalidEventError);
        return new DocumentationBuilder.Expression(currentCategory, patterns, type);
    }

    public static void registerCondition(Class<? extends Condition> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }

    public static DocumentationBuilder.Event registerEvent(String name, Class<? extends SkriptEvent> eventClass, Class<? extends Event> eventType, String... patterns) {
        Skript.registerEvent(name, eventClass, eventType, patterns);
        return new DocumentationBuilder.Event(currentCategory, patterns);
    }

    public static void registerScope(Class<? extends CustomScope> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }

    public static <E extends Event, R> void registerEventValue(Class<E> tClass, Class<R> rClass, Function<E, R> function) {
        EventValues.registerEventValue(tClass, rClass, new Getter<R, E>() {
            @Override
            public R get(E event) {
                try {
                    return function.apply(event);
                } catch (ClassCastException e) {
                    Logging.debug(Registration.class, "tClass = " + tClass + ", rClass = " + rClass + ", function = " + function);
                    Logging.debug(Registration.class, e);
                    return null;
                }
            }
        }, 0);
    }

    public static <A, B> void registerComparator(Class<A> aClass, Class<B> bClass, boolean supportsOrdering, BiFunction<A, B, Comparator.Relation> comparator) {
        Comparators.registerComparator(aClass, bClass, new Comparator<A, B>() {
            @Override
            public Relation compare(A a, B b) {
                return comparator.apply(a, b);
            }

            @Override
            public boolean supportsOrdering() {
                return supportsOrdering;
            }
        });
    }

    public static <F, T> void registerConverter(Class<F> from, Class<T> to, Function<F, T> function) {
        Converters.registerConverter(from, to, (Converter<F, T>) function::apply);
    }

    public static Boolean classInfoSafe(Class c, String name) {
        return Classes.getExactClassInfo(c) == null && Classes.getClassInfoNoError(name) == null;
    }

    public static <T> MundoClassInfo<T> registerType(Class<T> type, String name, String... alternateNames) {
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(alternateNames));
        names.add(0, name);
        MundoClassInfo<T> result = new MundoClassInfo<T>(type, names.toArray(new String[0]), currentCategory);
        result.parser(new SimpleParser<T>() {
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

    public static <E> EnumClassInfo<E> registerEnum(Class<E> enumClass, String name, E... values) {
        EnumClassInfo<E> enumClassInfo = new EnumClassInfo<E>(enumClass, new String[]{name}, currentCategory, values);
        ExprEnumValues.addEnumClassInfo(enumClassInfo);
        return enumClassInfo;
    }

    //Keys should be UPPERCASE
    public static <E> EnumClassInfo<E> registerEnum(Class<E> enumClass, String name, Map<String, E> valueMap) {
        EnumClassInfo<E> enumClassInfo = new EnumClassInfo<E>(enumClass, new String[]{name}, currentCategory, valueMap);
        ExprEnumValues.addEnumClassInfo(enumClassInfo);
        return enumClassInfo;
    }

    //Default pairing string names should be in uppercase
    /*public static <E> void registerEnum(Class<E> enumClass, String name, E[] values, Map.Entry<String, E>... defaultPairings) {
        if (!classInfoSafe(enumClass, name)) return;
        String[] usages = new String[values.length + defaultPairings.length];
        for (int i = 0; i < values.length; i++) {
            usages[i] = values[i].toString().toLowerCase();
        }
        for (int i = 0; i < defaultPairings.length; i++) {
            usages[i + values.length] = defaultPairings[i].getKey().toLowerCase();
        }
        Classes.registerClass(new ClassInfo<E>(enumClass, new String[]{name}, currentCategory, usages).user(new String[]{name}).name(name).parser(new Parser<E>() {
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
                return fields;
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
    }*/

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
