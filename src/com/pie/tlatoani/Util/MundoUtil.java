package com.pie.tlatoani.Util;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Checker;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Tlatoani on 8/10/17.
 */
public class MundoUtil {

    public static <T> boolean check(Expression<T> expression, Event event, Function<T, Boolean> function) {
        return expression.check(event, new Checker<T>() {
            @Override
            public boolean check(T t) {
                return function.apply(t);
            }
        });
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
}
