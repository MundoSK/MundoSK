package com.pie.tlatoani.CustomEvent;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.Reflection;
import com.pie.tlatoani.Util.Registration;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class CustomEventMundo {
    
    public static void load() {
        Registration.registerEffect(EffCallCustomEvent.class, "call custom event %string% [to] [det[ail]s %-objects%] [arg[ument]s %-objects%]");
        Registration.registerEvent("Custom Event", EvtCustomEvent.class, UtilCustomEvent.class, "ev[en]t %strings%");
        Registration.registerExpression(ExprIDOfCustomEvent.class,String.class, ExpressionType.PROPERTY,"id of custom event", "custom event's id");
        Registration.registerExpression(ExprArgsOfCustomEvent.class,Object.class,ExpressionType.PROPERTY,"args of custom event", "custom event's args");
        Registration.registerExpression(ExprLastCustomEventCancelled.class, Boolean.class, ExpressionType.SIMPLE, "last [called] custom event (0¦was|1¦wasn't) cancelled");

        try {
            //Field classinfos = Classes.class.getDeclaredField("tempClassInfos");
            //@SuppressWarnings("unchecked")
            //List<ClassInfo<?>> classes = (List<ClassInfo<?>>) classinfos.get(null);
            List<ClassInfo<?>> classes = (List<ClassInfo<?>>) Reflection.getStaticField(Classes.class, "tempClassInfos");
            for (int i = 0; i < classes.size(); i++) {
                registerCustomEventValue(classes.get(i));
            }
        } catch (Exception e1) {
            Logging.reportException(CustomEventMundo.class, e1);
        }
    }

    public static <T> void registerCustomEventValue(ClassInfo<T> type) {
        Registration.registerEventValue(UtilCustomEvent.class, type.getC(), e -> (T) e.getDetail(type));
    }
}
