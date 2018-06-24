package com.pie.tlatoani.Core.Static;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.variables.Variables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.pie.tlatoani.Util.AsymmetricComparator;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Created by Tlatoani on 8/10/17.
 */
public class Utilities {

    public static <T> boolean check(Expression<T> expression, Event event, Function<T, Boolean> function) {
        return expression.check(event, function::apply);
    }

    public static <T> boolean check(Expression<T> expression, Event event, Function<T, Boolean> function, boolean positive) {
        return expression.check(event, t -> positive == function.apply(t));
    }

    //ListVariable Util

    public static TreeMap<String, Object> listVariableFromArray(Object[] array) {
        TreeMap<String, Object> result = new TreeMap<>();
        for (int i = 1; i <= array.length; i++) {
            if (array[i] instanceof Object[]) {
                result.put(i + "::*", listVariableFromArray((Object[]) array[i]));
            } else if (array[i] instanceof TreeMap) {
                result.put(i + "::*", array[i]);
            } else {
                result.put(i + "", array[i]);
            }
        }
        return result;
    }

    public static void setListVariable(String varname, TreeMap<String, Object> value, Event event, boolean isLocal) {
        value.forEach((s, o) -> {
            if (o instanceof TreeMap) {
                setListVariable(varname + "::" + s, (TreeMap<String, Object>) o, event, isLocal);
            } else {
                Variables.setVariable(varname + "::" + s, o, event, isLocal);
            }
        });
    }

    //Miscellanous

    public static boolean serverHasPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    public static boolean classesCompatible(Class c1, Class c2) {
        return c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1);
    }

    public static Class commonSuperClass(Class... classes) {
        switch (classes.length) {
            case 0: return Object.class;
            case 1: return classes[0];
            case 2: {
                while (!classes[0].isAssignableFrom(classes[1])) {
                    classes[0] = classes[0].getSuperclass();
                }
                return classes[0];
            }
        }
        Class[] classesTail = new Class[classes.length - 1];
        System.arraycopy(classes, 0, classesTail, 0, classes.length - 1);
        return commonSuperClass(classes[0], commonSuperClass(classesTail));
    }

    public static <T, R> R[] mapArray(Function<T, R> function, T[] input) {
        return (R[]) Stream.of(input).map(function).collect(Collectors.toList()).toArray();
    }

    public static boolean posCurrentEvent(Class<? extends Event> event) {
        for (Class<? extends Event> currentEvent : ScriptLoader.getCurrentEvents()) {
            if (classesCompatible(event, currentEvent)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignableFromCurrentEvent(Class<?>... events) {
        for (Class<?> event : events) {
            for (Class<? extends Event> eventClass : ScriptLoader.getCurrentEvents()) {
                if (event.isAssignableFrom(eventClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getMundoCategory(Class<?> c) {
        return c.getName().split("\\.")[3];
    }

    public static <K, V> void sortListMultimap(ListMultimap<K, V> listMultimap, Comparator<? super V> comparator) {
        Multimaps.asMap(listMultimap).forEach((__, list) -> list.sort(comparator));
    }

    public static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static Optional<Integer> parseIntOptional(String posInt) {
        try {
            return Optional.of(Integer.parseInt(posInt));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static <T, U> Optional<U> binarySearchList(List<U> list, T value, AsymmetricComparator<T, U> comparator) {
        for (int low = 0, high = list.size() - 1, mid = (low + high) / 2; low <= high; mid = (low + high) / 2) {
            U pos = list.get(mid);
            int result = comparator.compare(value, pos);
            if (result == 0) {
                return Optional.of(pos);
            } else if (result > 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return Optional.empty();
    }

    public static <T, U> Optional<U> binarySearchCeiling(List<U> list, T value, AsymmetricComparator<T, U> comparator) {
        int low = 0, high = list.size() - 1;
        for (int mid = (low + high) / 2; low <= high; mid = (low + high) / 2) {
            U pos = list.get(mid);
            int result = comparator.compare(value, pos);
            if (result == 0) {
                return Optional.of(pos);
            } else if (result > 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return high == list.size() - 1 ? Optional.empty() : Optional.of(list.get(high + 1));
    }

    public static boolean wordsStartWith(String s1, String s2) {
        String[] words1 = s1.split(" ");
        String[] words2 = s2.split(" ");
        if (words1.length < words2.length) {
            return false;
        }
        for (int i = 0; i < words2.length; i++) {
            if (!words1[i].startsWith(words2[i])) {
                return false;
            }
        }
        return true;
    }

    //Optional

}
