package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptEventInfo;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Reflection;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class TestEventConstruction {

    public static void test() {
        try {
            Collection<SkriptEventInfo<?>> skriptEvents =
                    (Collection<SkriptEventInfo<?>>) Reflection.getStaticField(Skript.class, "events");
            Set<Class<? extends Event>> successfulEventClasses = new HashSet<>();
            Set<Class<? extends Event>> failedEventClasses = new HashSet<>();
            for (SkriptEventInfo<?> skriptEventInfo : skriptEvents) {
                for (Class<? extends Event> eventClass : skriptEventInfo.events) {
                    if (!successfulEventClasses.contains(eventClass) && !failedEventClasses.contains(eventClass)) {
                        if (testEventClass(eventClass)) {
                            successfulEventClasses.add(eventClass);
                        } else {
                            failedEventClasses.add(eventClass);
                        }
                    }
                }
            }
            Logging.debug(TestEventConstruction.class, "Out of " + (successfulEventClasses.size() + failedEventClasses.size()) + " event classes,");
            Logging.debug(TestEventConstruction.class, successfulEventClasses.size() + " succeeded and " + failedEventClasses.size() + " failed.");
            Logging.debug(TestEventConstruction.class, "The failures were " + failedEventClasses);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logging.reportException(TestEventConstruction.class, e);
        }
    }

    private static boolean testEventClass(Class<? extends Event> eventClass) {
        for (Constructor<?> constructor : eventClass.getConstructors()) {
            try {
                Object[] parameters = new Object[constructor.getParameterCount()];
                Class<?>[] paramTypes = constructor.getParameterTypes();
                for (int i = 0; i < parameters.length; i++) {
                    Class<?> paramType = paramTypes[i];
                    if (paramType == boolean.class || paramType == Boolean.class) {
                        parameters[i] = true;
                    } else if (paramType == int.class || paramType == Integer.class || paramType == Number.class) {
                        parameters[i] = (int) 0;
                    } else if (paramType == long.class || paramType == Integer.class) {
                        parameters[i] = (long) 0;
                    } else if (paramType == double.class || paramType == Double.class) {
                        parameters[i] = (double) 0;
                    } else if (paramType == short.class || paramType == Short.class) {
                        parameters[i] = (short) 0;
                    } else if (paramType == float.class || paramType == Float.class) {
                        parameters[i] = (float) 0;
                    } else if (paramType == byte.class || paramType == Byte.class) {
                        parameters[i] = (byte) 0;
                    } else if (paramType == String.class) {
                        parameters[i] = "abcd";
                    } else if (paramType.isArray()) {
                        parameters[i] = Array.newInstance(paramType.getComponentType(), 1);
                    } else if (paramType.isEnum()) {
                        parameters[i] = paramType.getEnumConstants()[0];
                    }
                }
                Object creation = constructor.newInstance(parameters);
                StringJoiner logBuilder = new StringJoiner(
                        ", ",
                        ChatColor.DARK_GREEN + "Success, creation: " + creation + ", constructor: " + eventClass.getName() + "(",
                        ")"
                );
                for (Class<?> paramType : paramTypes) {
                    logBuilder.add(paramType.getName());
                }
                Logging.debug(TestEventConstruction.class, logBuilder.toString());
                return true;
            } catch (Exception e) {
                StringJoiner logBuilder = new StringJoiner(
                        ", ",
                        "Exception, constructor: " + eventClass.getName() + "(",
                        ")"
                );
                for (Class<?> paramType : constructor.getParameterTypes()) {
                    logBuilder.add(paramType.getName());
                }
                Logging.debug(TestEventConstruction.class, logBuilder.toString());
                Logging.debug(TestEventConstruction.class, e);
            }
        }
        Logging.info(ChatColor.RED + "Failure for class " + eventClass.getName());
        return false;
    }
}
