package com.pie.tlatoani.ListUtil;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 6/10/16.
 */
public final class ListUtil {
    private final static Map<String, Class<? extends Transformer>> dictionary = new HashMap<String, Class<? extends Transformer>>();

    //Cannot be instantiated
    private ListUtil() {}

    public static void registerTransformer(Class<? extends Transformer> transformer, String... patterns) {
        for (int i = 0; i < patterns.length; i++) {
            dictionary.putIfAbsent(patterns[i], transformer);
        }
    }

    public static Transformer retrieveTransformer(String pattern, Expression expression) {
        Transformer transformer = null;
        if (expression instanceof PersonalTransformer) {
            Transformer personalTransformer = ((PersonalTransformer) expression).getPersonalTransformer(pattern);
            if (personalTransformer != null ) {
                return personalTransformer;
            }
        }
        try {
            transformer = dictionary.get(pattern).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        if (transformer == null) {
            Skript.error("'" + pattern + (expression != null ? "s of " + expression + "'" : "s'") + " is not a list!");
            return null;
        }
        return transformer.init(expression) ? transformer : null;
    }

    public static Transformer retrieveTransformerByPlural(String pattern, Expression expression) {
        if (pattern.charAt(pattern.length() - 1) == 's') {
            return retrieveTransformer(pattern.substring(0, pattern.length() - 1), expression);
        }
        Skript.error("'" + pattern + (expression != null ? " of " + expression + "'" : "'") + " is not a list!");
        return null;
    }

    public interface TransformerUser {

        Transformer getTransformer();
    }

    public interface PersonalTransformer {

        Transformer getPersonalTransformer(String pattern);
    }

    public interface Moveable {

        void move(Event event, Integer movement);

        Boolean isMoveable();
    }
}
