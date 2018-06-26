package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptEventInfo;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Reflection;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

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
            Logging.info("Out of " + (successfulEventClasses.size() + failedEventClasses.size()) + " event classes,");
            Logging.info(successfulEventClasses.size() + " succeeded and " + failedEventClasses.size() + " failed.");
            Logging.info("The failures were " + failedEventClasses);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logging.reportException(TestEventConstruction.class, e);
        }
    }

    private static boolean testEventClass(Class<? extends Event> eventClass) {
        for (Constructor<?> constructor : eventClass.getConstructors()) {
            try {
                Object[] parameters = new Object[constructor.getParameterCount()];
                Object creation = constructor.newInstance(parameters);
                StringJoiner logBuilder = new StringJoiner(
                        ", ",
                        ChatColor.DARK_GREEN + "Success, creation: " + creation + ", constructor: " + eventClass.getName() + "(",
                        ")"
                );
                for (Class<?> paramType : constructor.getParameterTypes()) {
                    logBuilder.add(paramType.getName());
                }
                Logging.info(logBuilder.toString());
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
                Logging.info(logBuilder.toString());
                Logging.debug(TestEventConstruction.class, e);
            }
        }
        Logging.info(ChatColor.RED + "Failure for class " + eventClass.getName());
        return false;
    }
}
